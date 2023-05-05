package nl.tnt.assessment.client;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Client<T, REQUEST extends ClientRequest<T>, RESPONSE extends ClientResponse<T>> {

    private final WebClient webClient;
    private final Deque<ClientRequest<T>> deque = new ArrayDeque<>();

    private final String queryParamName;
    private final int queueCap;

    public Client(String url, String queryParamName, int queueCap) {
        this.webClient = WebClient.builder().baseUrl(url).build();
        this.queryParamName = queryParamName;
        this.queueCap = queueCap;
    }

    protected abstract ClientRequest<T> getRequest(List<String> orderNumbers);
    protected abstract ClientResponse<T> getResponse(List<String> orderNumbers);


    public Map<String, T> get(List<String> orderNumbers){
        final ClientRequest<T> request = getRequest(orderNumbers);
        deque.add(request);
        checkDeque();
        return request.getResult();
    }

    private void checkDeque() {
        final List<String> orderNumbers = deque.stream()
            .flatMap(request -> request.getOrderNumbers().stream())
            .distinct()
            .collect(Collectors.toList());
        if (orderNumbers.size() >= queueCap) {
            // todo: check 2nd story-functionality! Here we process all requests in the queue if the cap is hit, why limit it to only 5??
            processDeque(orderNumbers);
        }
    }

    private void processDeque(List<String> orderNumbers) {
        final Map<String, T> responses = callClient(orderNumbers);
        deque.forEach(pricingRequest -> pricingRequest.complete(responses));
        deque.clear();
    }

    private Map<String, T> callClient(List<String> orderNumbers) {
        return getResponse(orderNumbers).getResult();
    }

    protected WebClient.ResponseSpec responseSpec(List<String> orderNumbers){
        final String values = String.join(",", orderNumbers);
        return webClient
            .get()
            .uri(uriBuilder -> uriBuilder.queryParam(queryParamName, values).build())
            .retrieve();
    }

}
