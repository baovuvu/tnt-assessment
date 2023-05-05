package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingClient {

    private final WebClient webClient;
    private final Deque<PricingRequest> deque = new ArrayDeque<>();

    @Value("${client.pricing.queryParamName}")
    private String queryParamName;
    @Value("${client.pricing.queueCap}")
    private int queueCap;

    public PricingClient(@Value("${client.pricing.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public Map<String, Float> get(List<String> orderNumbers){
        final PricingRequest pricingRequest = new PricingRequest(orderNumbers);// PricingRequest.builder().orderNumbers(orderNumbers).build();
//        final PricingRequest pricingRequest = PricingRequest.builder().orderNumbers(orderNumbers).build();
        deque.add(pricingRequest);
        checkDeque();
        return pricingRequest.getResult();
    }

    private void checkDeque() {
        final List<String> orders = deque.stream()
            .flatMap(pricingRequest -> pricingRequest.getOrderNumbers().stream())
            .distinct()
            .collect(Collectors.toList());
        if (orders.size() >= queueCap) {
            // todo: check 2nd story-functionality! Here we process all requests in the queue if the cap is hit, why limit it to only 5??
            processDeque(orders);
        }
    }

    private void processDeque(List<String> orders) {
        final Map<String, Float> shipments = callClient(orders);
        deque.forEach(pricingRequest -> pricingRequest.complete(shipments));
        deque.clear();
    }

    private Map<String, Float> callClient(List<String> orderNumbers){
        final String values = String.join(",", orderNumbers);
//        final String test = webClient
//            .get()
//            .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
//            .retrieve()
//            .bodyToMono(String.class)
//            .block();
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            return objectMapper.readValue(test, PricingResponse.class).getResult();
//        } catch (JsonProcessingException e) {
//            throw new RuntimeException(e);
//        }
        final PricingResponse response = webClient
            .get()
            .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
            .retrieve()
            .bodyToMono(PricingResponse.class)
            .block();
        return response.getResult();
    }

}
