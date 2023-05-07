package nl.tnt.assessment.client;

import java.util.List;

public class PricingRequest extends ClientRequest<Float> {

    public PricingRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

}
