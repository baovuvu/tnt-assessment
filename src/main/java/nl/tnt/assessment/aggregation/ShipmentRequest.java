package nl.tnt.assessment.aggregation;

import lombok.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class ShipmentRequest {

    private List<String> orderNumbers;
    private CompletableFuture<List<Shipment>> futureResult;

    public void complete(List<Shipment> shipments) {
        final List<Shipment> result = shipments.stream()
            .filter(shipment -> orderNumbers.contains(shipment.getOrderNumber()))
            .collect(Collectors.toList());
        futureResult.complete(result);
    }
}
