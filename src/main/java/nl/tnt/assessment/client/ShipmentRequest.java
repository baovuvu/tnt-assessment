package nl.tnt.assessment.client;

import java.util.List;

public class ShipmentRequest extends ClientRequest<List<String>> {

    public ShipmentRequest(List<String> orderNumbers) {
        super(orderNumbers, new ShipmentResponse());
    }

}
