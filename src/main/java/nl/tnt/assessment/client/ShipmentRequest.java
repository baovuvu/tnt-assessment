package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ShipmentRequest extends ClientRequest<List<String>> {

    public ShipmentRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

}
