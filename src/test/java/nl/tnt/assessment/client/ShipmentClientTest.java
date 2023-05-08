package nl.tnt.assessment.client;

import nl.tnt.assessment.util.Utils;
import org.mockito.InjectMocks;

import java.util.List;

import static nl.tnt.assessment.util.Utils.setPrivateField;

class ShipmentClientTest extends ClientTest<ShipmentClient, ShipmentRequest, ShipmentResponse>{
    @InjectMocks
    private ShipmentClient client = new ShipmentClient("", "", 0, 5);

    @Override
    protected ShipmentClient getClient(String url) {
        client = new ShipmentClient(url, "q", 5, 5);
        return client;
    }

    @Override
    protected List<String> getOrders() {
        return Utils.getList(testObject.getShipmentsParam());
    }

    @Override
    protected ShipmentRequest getRequest(List<String> orders) {
        return new ShipmentRequest(orders);
    }

    @Override
    protected ShipmentResponse getResponse() {
        final ShipmentResponse response = new ShipmentResponse();
        setPrivateField(response, "result", testObject.getShipments());
        return response;
    }
}
