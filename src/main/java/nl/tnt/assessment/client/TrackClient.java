package nl.tnt.assessment.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrackClient extends Client<String, TrackRequest, TrackResponse> {

    public TrackClient(@Value("${client.track.url}") String url
        , @Value("${client.track.queryParamName}") String queryParamName
        , @Value("${client.track.queueCap}") int queueCap
        , @Value("${client.track.queueSeconds}") int queueSeconds) {
        super(url, queryParamName, queueCap, queueSeconds);
    }

    @Override
    protected TrackRequest getRequest(List<String> orders) {
        return new TrackRequest(orders);
    }

    @Override
    protected Class<TrackResponse> getResponseClass() {
        return TrackResponse.class;
    }

    @Override
    protected TrackResponse getResponse() {
        return new TrackResponse();
    }

}
