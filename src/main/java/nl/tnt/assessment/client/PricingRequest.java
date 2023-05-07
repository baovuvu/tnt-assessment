package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PricingRequest extends ClientRequest<Float> {

    public PricingRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

}
