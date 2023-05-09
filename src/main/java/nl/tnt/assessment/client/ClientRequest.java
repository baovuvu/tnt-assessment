package nl.tnt.assessment.client;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Getter
public abstract class ClientRequest<T> {

    private final List<String> orders;
    private final CompletableFuture<ClientResponse<T>> futureResult = new CompletableFuture<>();
    private final LocalDateTime queueDate = LocalDateTime.now();

    public ClientRequest(List<String> orders) {
        this.orders = orders;
    }

    public ClientRequest<T> complete(ClientResponse<T> response) {
        futureResult.complete(response);
        return this;
    }

    public Optional<ClientResponse<T>> getResponse() {
        try {
            // futureResult.get() will holds execution of the code until it's completed!
            return Optional.ofNullable(futureResult.get());
        } catch (InterruptedException | ExecutionException e) {
            return Optional.empty();
        }
    }

}
