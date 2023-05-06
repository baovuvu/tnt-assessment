package nl.tnt.assessment.client;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TrackRequest extends ClientRequest<String> {

    public TrackRequest(List<String> orderNumbers) {
        super(orderNumbers);
    }

    @Override
    protected Map<String, String> getResult(Map<String, String> response) {
        return response.entrySet().stream()
            .filter(entry -> getOrderNumbers().contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
