package nl.tnt.assessment.client;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public abstract class ClientRequest<T> {

    private final List<String> orderNumbers;
    private final CompletableFuture<Map<String, T>> futureResult = new CompletableFuture<>();

    public ClientRequest(List<String> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }

    public void complete(ClientResponse<T> response) {
        futureResult.complete(response == null ? getNullResult() : response.getResult(orderNumbers));
    }

    private Map<String, T> getNullResult() {
        final Map<String, T> result = new HashMap<>();
        orderNumbers.forEach(orderNumber -> result.put(orderNumber, null));
        return result;
    }

    public Map<String, T> getResult() {
        try {
            return futureResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
