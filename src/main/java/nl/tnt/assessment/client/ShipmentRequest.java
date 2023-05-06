package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShipmentRequest extends ClientRequest<List<String>> {

    public ShipmentRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

    @Override
    protected Map<String, List<String>> getResult(Map<String, List<String>> response) {
        return response.entrySet().stream()
            .filter(entry -> getOrderNumbers().contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
