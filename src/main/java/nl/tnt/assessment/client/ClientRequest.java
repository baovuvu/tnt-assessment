package nl.tnt.assessment.client;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public abstract class ClientRequest<T> {

    private final List<String> orderNumbers;
    private final CompletableFuture<ClientResponse<T>> futureResult = new CompletableFuture<>();

    public ClientRequest(List<String> orderNumbers) {
        this.orderNumbers = orderNumbers;
    }

    public void complete(ClientResponse<T> response) {
        futureResult.complete(response);
    }

    public ClientResponse<T> getResult() {
        try {
            return futureResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
