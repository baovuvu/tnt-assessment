package nl.tnt.assessment.aggregation;

import nl.tnt.assessment.client.PricingClient;
import nl.tnt.assessment.client.ShipmentClient;
import nl.tnt.assessment.client.TrackClient;
import nl.tnt.assessment.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregationControllerTest {

    @InjectMocks
    private AggregationController aggregationController;

    @Mock
    private ShipmentClient shipmentClient;
    @Mock
    private TrackClient trackClient;
    @Mock
    private PricingClient pricingClient;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(shipmentClient, trackClient, pricingClient);
    }

    @Test
    void get() {
        test(Utils.testObject());
    }

    @Test
    void getNull() {
        final AggregationResponse test = AggregationResponse.builder()
            .shipments(new HashMap<>())
            .pricing(new HashMap<>())
            .track(new HashMap<>())
            .build();
        test(test);
    }

    @Test
    void getEmpty() {
        final AggregationResponse test = AggregationResponse.builder()
            .shipmentsParam("")
            .trackParam(" ")
            .pricingParam("")
            .shipments(new HashMap<>())
            .pricing(new HashMap<>())
            .track(new HashMap<>())
            .build();
        test(test);
    }

    private void test(AggregationResponse test) {
        when(shipmentClient.get(Utils.getList(test.getShipmentsParam()))).thenReturn(test.getShipments());
        when(trackClient.get(Utils.getList(test.getTrackParam()))).thenReturn(test.getTrack());
        when(pricingClient.get(Utils.getList(test.getPricingParam()))).thenReturn(test.getPricing());

        final AggregationResponse response = aggregationController.get(test.getShipmentsParam(), test.getTrackParam(), test.getPricingParam());
        assertEquals(test, response);
    }
}