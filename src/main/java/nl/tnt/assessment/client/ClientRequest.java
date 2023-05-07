package nl.tnt.assessment.client;

import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public abstract class ClientRequest<T> {

    private final List<String> orders;
    private final CompletableFuture<ClientResponse<T>> futureResult = new CompletableFuture<>();

    public ClientRequest(List<String> orders) {
        this.orders = orders;
    }

    public void complete(ClientResponse<T> response) {
        futureResult.complete(response);
    }

    public Optional<ClientResponse<T>> getResponse() {
        try {
            return Optional.ofNullable(futureResult.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

}
