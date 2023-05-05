package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PricingRequest extends ClientRequest<Float> {

    public PricingRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

    @Override
    public void complete(Map<String, Float> shipments) {
        final Map<String, Float> result = shipments.entrySet().stream()
            .filter(shipment -> getOrderNumbers().contains(shipment.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        getFutureResult().complete(result);
    }

}
