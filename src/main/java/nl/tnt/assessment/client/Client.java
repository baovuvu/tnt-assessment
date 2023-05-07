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
    private final Deque<REQUEST> deque = new ArrayDeque<>();

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
        final REQUEST request = getRequest(orderNumbers);
        deque.add(request);
        checkDeque();
        return Optional.ofNullable(request.getResult())
            .map(clientResponse -> clientResponse.getResult(orderNumbers))
            .orElse(getNullResult(orderNumbers));
    }

    private Map<String, T> getNullResult(List<String> orderNumbers) {
        final Map<String, T> result = new HashMap<>();
        orderNumbers.forEach(orderNumber -> result.put(orderNumber, null));
        return result;
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
        final RESPONSE response = callClient(orderNumbers);
        deque.forEach(request -> request.complete(response));
        deque.clear();
    }

    private RESPONSE callClient(List<String> orderNumbers) {
        final String values = String.join(",", orderNumbers);
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, status -> Mono.error(new RuntimeException()))
                .bodyToMono(getResponseClass())
                .block();
        } catch (Exception e) {
            return null;
        }
    }

}
