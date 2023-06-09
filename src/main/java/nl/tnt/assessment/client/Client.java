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
        // request.getResponse() will holds execution of the code while the request is not yet completed (i.e. the request is still in the queue)!
        return request.getResponse().orElse(getResponse()).getResult(orders);
    }

    public void processQueue() {
        queue.stream()
            .filter(request -> request.getQueueDate().plusSeconds(queueSeconds).isBefore(LocalDateTime.now()))
            .findFirst()
            .ifPresent(request -> processAndClearQueue());
    }

    private REQUEST addToQueue(List<String> orders) {
        LOGGER.info(String.format("%s addToQueue: %s", this.getClass().getSimpleName(), orders.toString()));
        final REQUEST request = getRequest(orders);
        queue.add(request);
        final List<String> allOrders = getRequestOrders(queue);
        LOGGER.info(String.format("%s Queue: %s", this.getClass().getSimpleName(), allOrders));
        if (allOrders.size() >= queueCap) processAndClearQueue();
        return request;
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    private void processAndClearQueue() {
        final List<REQUEST> requests = new ArrayList<>(queue);
        final List<String> orders = getRequestOrders(requests);
        LOGGER.info(String.format("%s processQueue: %s", this.getClass().getSimpleName(), orders.toString()));
        final RESPONSE response = callClient(orders);
        requests.stream()
            .map(request -> request.complete(response))
            .forEach(queue::remove);// remove the requests from the queue instead of queue.clear() to avoid concurrency issues...
    }

    private List<String> getRequestOrders(List<REQUEST> requests) {
        return requests.stream()
            .flatMap(item -> item.getOrders().stream())
            .collect(Collectors.toList());
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
