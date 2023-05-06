package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PricingRequest extends ClientRequest<Float> {

    public PricingRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

    @Override
    protected Map<String, Float> getResult(Map<String, Float> response) {
        return response.entrySet().stream()
            .filter(entry -> getOrderNumbers().contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
