package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class PricingClient extends Client<Float, PricingRequest, PricingResponse> {

    public PricingClient(@Value("${client.pricing.url}") String url
        , @Value("${client.pricing.queryParamName}") String queryParamName
        , @Value("${client.pricing.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected PricingRequest getRequest(List<String> orderNumbers) {
        return new PricingRequest(orderNumbers);
    }

    @Override
    protected Mono<PricingResponse> getResponseBody(WebClient.ResponseSpec responseSpec) {
        return responseSpec
            .bodyToMono(PricingResponse.class);}

}
