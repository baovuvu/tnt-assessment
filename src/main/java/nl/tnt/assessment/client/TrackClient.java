package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TrackClient extends Client<String, TrackRequest, TrackResponse> {

    public TrackClient(@Value("${client.track.url}") String url
        , @Value("${client.track.queryParamName}") String queryParamName
        , @Value("${client.track.queueCap}") int queueCap) {
        super(url, queryParamName, queueCap);
    }

    @Override
    protected TrackRequest getRequest(List<String> orderNumbers) {
        return new TrackRequest(orderNumbers);
    }

    @Override
    protected Mono<TrackResponse> getResponseBody(WebClient.ResponseSpec responseSpec) {
        return responseSpec.bodyToMono(TrackResponse.class);
    }

}
