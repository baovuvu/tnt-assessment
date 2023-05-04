package nl.tnt.assessment.aggregation;

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
    private CompletableFuture<List<Shipment>> futureResult;

    public static ShipmentRequest create(List<String> orderNumbers){
        final CompletableFuture<List<Shipment>> completableFuture = new CompletableFuture<>();
        return ShipmentRequest.builder()
            .orderNumbers(orderNumbers)
            .futureResult(completableFuture)
            .build();
    }

    public void complete(List<Shipment> shipments) {
        final List<Shipment> result = shipments.stream()
            .filter(shipment -> orderNumbers.contains(shipment.getOrderNumber()))
            .collect(Collectors.toList());
        futureResult.complete(result);
    }

    public Map<String, List<String>> getResult(){
        try {
            return futureResult.get().stream()
                .collect(Collectors.toMap(Shipment::getOrderNumber, Shipment::getProducts));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
