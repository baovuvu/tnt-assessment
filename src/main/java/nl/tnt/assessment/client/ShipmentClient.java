package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipmentClient extends Client<List<String>, ShipmentRequest, ShipmentResponse> {

    public ShipmentClient(@Value("${client.shipmemt.url}") String url
        , @Value("${client.shipmemt.queryParamName}") String queryParamName
        , @Value("${client.shipmemt.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected ShipmentRequest getRequest(List<String> orders) {
        return new ShipmentRequest(orders);
    }

    @Override
    protected Class<ShipmentResponse> getResponseClass() {
        return ShipmentResponse.class;
    }

    @Override
    protected ShipmentResponse getResponse() {
        return new ShipmentResponse();
    }

}
