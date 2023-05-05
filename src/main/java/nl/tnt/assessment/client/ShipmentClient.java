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
public class ShipmentClient {

    private final WebClient webClient;
    private final Deque<ShipmentRequest> deque = new ArrayDeque<>();

    @Value("${client.shipmemt.queryParamName}")
    private String queryParamName;
    @Value("${client.shipmemt.queueCap}")
    private int queueCap;

    public ShipmentClient(@Value("${client.shipmemt.url}") String url) {
        this.webClient = WebClient.builder().baseUrl(url).build();
    }

    public Map<String, List<String>> get(List<String> orderNumbers) {
        final ShipmentRequest shipmentRequest = new ShipmentRequest(orderNumbers);
        deque.add(shipmentRequest);
        checkDeque();
        return shipmentRequest.getResult();
    }

    private void checkDeque() {
        final List<String> orders = deque.stream()
            .flatMap(shipmentRequest -> shipmentRequest.getOrderNumbers().stream())
            .distinct()
            .collect(Collectors.toList());
        if (orders.size() >= queueCap) {
            // todo: check 2nd story-functionality! Here we process all requests in the queue if the cap is hit, why limit it to only 5??
            processDeque(orders);
        }
    }

    private void processDeque(List<String> orders) {
        final Map<String, List<String>> shipments = callClient(orders);
        deque.forEach(shipmentRequest -> shipmentRequest.complete(shipments));
        deque.clear();
    }

    private Map<String, List<String>> callClient(List<String> orderNumbers) {
        final String values = String.join(",", orderNumbers);
        final ShipmentResponse response = webClient
            .get()
            .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
            .retrieve()
            .bodyToMono(ShipmentResponse.class)
            .block();
        return response.getResult();
    }

}
