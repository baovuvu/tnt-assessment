package nl.tnt.assessment.client;

import java.util.List;

public class TrackRequest extends ClientRequest<String> {

    public TrackRequest(List<String> orderNumbers) {
        super(orderNumbers, new TrackResponse());
    }

}
