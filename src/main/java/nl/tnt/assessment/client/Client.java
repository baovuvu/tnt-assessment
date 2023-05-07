package nl.tnt.assessment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Client<T, REQUEST extends ClientRequest<T>, RESPONSE extends ClientResponse<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private final WebClient webClient;
    private final Deque<ClientRequest<T>> deque = new ArrayDeque<>();

    private final String queryParamName;
    private final int queueCap;

    public Client(String url, String queryParamName, int queueCap) {
        this.webClient = WebClient.builder().baseUrl(url).build();
        this.queryParamName = queryParamName;
        this.queueCap = queueCap;
    }

    public Map<String, T> get(List<String> orderNumbers) {
        LOGGER.info(String.format("%s get: %s", this.getClass().getSimpleName(), orderNumbers.toString()));
        if (orderNumbers.isEmpty()) return new HashMap<>();
        final ClientRequest<T> request = getRequest(orderNumbers);
        deque.add(request);
        checkDeque();
        return request.getResult();
    }

    protected abstract REQUEST getRequest(List<String> orderNumbers);

    protected abstract Class<RESPONSE> getResponseClass();

    private void checkDeque() {

        final List<String> orderNumbers = deque.stream()
            .flatMap(request -> request.getOrderNumbers().stream())
//            .distinct()
            .collect(Collectors.toList());
        if (orderNumbers.size() >= queueCap) {
            // todo: check 2nd story-functionality! Here we process all requests in the queue if the cap is hit, why limit it to only 5??
            processDeque(orderNumbers);
        }
    }

    private void processDeque(List<String> orderNumbers) {
        LOGGER.info(String.format("%s processDeque: %s", this.getClass().getSimpleName(), orderNumbers.toString()));
        final Map<String, T> responses = callClient(orderNumbers);
        deque.forEach(pricingRequest -> pricingRequest.complete(responses));
        deque.clear();
    }

    private Map<String, T> callClient(List<String> orderNumbers) {
        final String values = String.join(",", orderNumbers);
        try {
            final RESPONSE response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, status -> Mono.error(new RuntimeException()))
                .bodyToMono(getResponseClass())
                .block();
            return Optional.ofNullable(response).map(s -> s.getResult()).orElse(null);
        } catch (Exception e) {
            return null;
        }
    }

}
