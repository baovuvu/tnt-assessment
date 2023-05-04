package nl.tnt.assessment.client;

import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class ShipmentRequest {

    private List<String> orderNumbers;
    private CompletableFuture<Map<String, List<String>>> futureResult;

    public static ShipmentRequest create(List<String> orderNumbers){
        final CompletableFuture<Map<String, List<String>>> completableFuture = new CompletableFuture<>();
        return ShipmentRequest.builder()
            .orderNumbers(orderNumbers)
            .futureResult(completableFuture)
            .build();
    }

    public void complete(Map<String, List<String>> shipments) {
        final Map<String, List<String>> result = shipments.entrySet().stream()
            .filter(shipment -> orderNumbers.contains(shipment.getKey()))
            .collect(Collectors.toMap(entry -> entry.getKey(), Map.Entry::getValue));
        futureResult.complete(result);
    }

    public Map<String, List<String>> getResult(){
        try {
            return futureResult.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
