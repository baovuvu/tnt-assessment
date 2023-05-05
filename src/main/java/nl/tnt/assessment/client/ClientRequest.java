package nl.tnt.assessment.client;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Getter
public abstract class ClientRequest<T> {

    private List<String> orderNumbers;
    private CompletableFuture<Map<String, T>> futureResult = new CompletableFuture<>();

    public ClientRequest(List<String> orderNumbers){
        this.orderNumbers = orderNumbers;
    }

    public abstract void complete(Map<String, T> shipments);

    public Map<String, T> getResult(){
        try {
            return futureResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
