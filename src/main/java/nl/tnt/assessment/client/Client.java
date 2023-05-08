package nl.tnt.assessment.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Client<T, REQUEST extends ClientRequest<T>, RESPONSE extends ClientResponse<T>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Client.class);
    private final WebClient webClient;
    private final List<REQUEST> queue = new ArrayList<>();

    private final String queryParamName;
    private final int queueCap;
    private final int queueSeconds;

    public Client(String url, String queryParamName, int queueCap, int queueSeconds) {
        this.webClient = WebClient.builder().baseUrl(url).build();
        this.queryParamName = queryParamName;
        this.queueCap = queueCap;
        this.queueSeconds = queueSeconds;
    }

    protected abstract REQUEST getRequest(List<String> orders);

    protected abstract Class<RESPONSE> getResponseClass();

    protected abstract RESPONSE getResponse();

    public Map<String, T> get(List<String> orders) {
        LOGGER.info(String.format("%s get: %s", this.getClass().getSimpleName(), orders.toString()));
        if (orders.isEmpty()) return new HashMap<>();
        final REQUEST request = addToQueue(orders);
        return request.getResponse().orElse(getResponse()).getResult(orders);
    }

    public void processQueue() {
        queue.stream()
            .filter(request -> request.getQueueDate().plusSeconds(queueSeconds).isBefore(LocalDateTime.now()))
            .findFirst()
            .ifPresent(request -> processQueue(getRequestOrders()));
    }

    private REQUEST addToQueue(List<String> orders) {
        LOGGER.info(String.format("%s addToQueue: %s", this.getClass().getSimpleName(), orders.toString()));
        final REQUEST request = getRequest(orders);
        queue.add(request);
        final List<String> allOrders = getRequestOrders();
        LOGGER.info(String.format("%s Queue: %s", this.getClass().getSimpleName(), allOrders));
        if (allOrders.size() >= queueCap) processQueue(allOrders);
        return request;
    }

    private List<String> getRequestOrders() {
        return queue.stream()
            .flatMap(item -> item.getOrders().stream())
            .collect(Collectors.toList());
    }

    private void processQueue(List<String> orders) {
        LOGGER.info(String.format("%s processQueue: %s", this.getClass().getSimpleName(), orders.toString()));
        final RESPONSE response = callClient(orders);
        queue.forEach(request -> request.complete(response));
        queue.clear();
    }

    private RESPONSE callClient(List<String> orders) {
        LOGGER.info(String.format("%s callClient: %s", this.getClass().getSimpleName(), orders.toString()));
        final String values = String.join(",", orders);
        try {
            return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
                .retrieve()
                .onStatus(HttpStatusCode::is5xxServerError, status -> Mono.error(new Exception("Service unavailable")))
                .bodyToMono(getResponseClass())
                .block();
        } catch (Exception e) {
            LOGGER.info(String.format("%s callClient: %s", this.getClass().getSimpleName(), e.getMessage()));
            return null;
        }
    }

}
