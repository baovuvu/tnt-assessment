package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShipmentClient extends Client<List<String>, ShipmentRequest, ShipmentResponse> {

    public ShipmentClient(@Value("${client.shipmemt.url}") String url
        , @Value("${client.shipmemt.queryParamName}") String queryParamName
        , @Value("${client.shipmemt.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected ClientRequest<List<String>> getRequest(List<String> orderNumbers) {
        return new ShipmentRequest(orderNumbers);
    }

    @Override
    protected ClientResponse<List<String>> getResponse(List<String> orderNumbers) {
        return responseSpec(orderNumbers)
            .bodyToMono(ShipmentResponse.class)
            .block();
    }

}
