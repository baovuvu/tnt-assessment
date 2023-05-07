package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PricingClient extends Client<Float, PricingRequest> {

    public PricingClient(@Value("${client.pricing.url}") String url
        , @Value("${client.pricing.queryParamName}") String queryParamName
        , @Value("${client.pricing.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected PricingRequest getRequest(List<String> orderNumbers) {
        return new PricingRequest(orderNumbers);
    }

}
