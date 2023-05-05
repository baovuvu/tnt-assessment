package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PricingClient extends Client<Float, PricingRequest, PricingResponse> {

    public PricingClient(@Value("${client.pricing.url}") String url
        , @Value("${client.pricing.queryParamName}") String queryParamName
        , @Value("${client.pricing.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected ClientRequest<Float> getRequest(List<String> orderNumbers) {
        return new PricingRequest(orderNumbers);
    }

    @Override
    protected ClientResponse<Float> getResponse(List<String> orderNumbers) {
        return responseSpec(orderNumbers)
            .bodyToMono(PricingResponse.class)
            .block();
    }

}
