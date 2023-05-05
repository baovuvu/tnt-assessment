package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShipmentRequest extends ClientRequest<List<String>> {

    public ShipmentRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

    @Override
    public void complete(Map<String, List<String>> shipments) {
        final Map<String, List<String>> result = shipments.entrySet().stream()
            .filter(shipment -> getOrderNumbers().contains(shipment.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        getFutureResult().complete(result);
    }

}
