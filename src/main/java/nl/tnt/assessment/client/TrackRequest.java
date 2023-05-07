package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrackRequest extends ClientRequest<String> {

    public TrackRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

}
