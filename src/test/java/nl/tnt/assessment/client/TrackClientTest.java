package nl.tnt.assessment.client;

import nl.tnt.assessment.util.Utils;
import org.mockito.InjectMocks;

import java.util.List;

import static nl.tnt.assessment.util.Utils.setPrivateField;

class TrackClientTest extends ClientTest<TrackClient, TrackRequest, TrackResponse>{
    @InjectMocks
    private TrackClient client = new TrackClient("", "", 0, 5);

    @Override
    protected TrackClient getClient(String url) {
        client = new TrackClient(url, "q", 5, 5);
        return client;
    }

    @Override
    protected List<String> getOrders() {
        return Utils.getList(testObject.getTrackParam());
    }

    @Override
    protected TrackRequest getRequest(List<String> orders) {
        return new TrackRequest(orders);
    }

    @Override
    protected TrackResponse getResponse() {
        final TrackResponse response = new TrackResponse();
        setPrivateField(response, "result", testObject.getTrack());
        return response;
    }
}
