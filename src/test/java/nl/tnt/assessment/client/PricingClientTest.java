package nl.tnt.assessment.client;

import nl.tnt.assessment.util.Utils;
import org.mockito.InjectMocks;

import java.util.List;

import static nl.tnt.assessment.util.Utils.setPrivateField;

class PricingClientTest extends ClientTest<PricingClient, PricingRequest, PricingResponse>{
    @InjectMocks
    private PricingClient client = new PricingClient("", "", 0, 5);

    @Override
    protected PricingClient getClient(String url) {
        client = new PricingClient(url, "q", 5, 5);
        return client;
    }

    @Override
    protected List<String> getOrders() {
        return Utils.getList(testObject.getPricingParam());
    }

    @Override
    protected PricingRequest getRequest(List<String> orders) {
        return new PricingRequest(orders);
    }

    @Override
    protected PricingResponse getResponse() {
        final PricingResponse response = new PricingResponse();
        setPrivateField(response, "result", testObject.getPricing());
        return response;
    }
}
