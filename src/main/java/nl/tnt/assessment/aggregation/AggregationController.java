package nl.tnt.assessment.aggregation;

import nl.tnt.assessment.client.PricingClient;
import nl.tnt.assessment.client.ShipmentClient;
import nl.tnt.assessment.client.TrackClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentClient shipmentClient;
    private final PricingClient pricingClient;
    private final TrackClient trackClient;

    private final List<Function<AggregationResponse, AggregationResponse>> setters = List.of(this::setShipments, this::setTrack, this::setPricing);

    public AggregationController(ShipmentClient shipmentClient, PricingClient pricingClient, TrackClient trackClient) {
        this.shipmentClient = shipmentClient;
        this.pricingClient = pricingClient;
        this.trackClient = trackClient;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) {
        final AggregationResponse response = AggregationResponse.builder()
            .shipmentsParam(shipments)
            .trackParam(track)
            .pricingParam(pricing)
            .build();
        // setting the results from the clients asynchronously:
        setters.stream().parallel().forEach(f -> f.apply(response));
        return response;
//        return AggregationResponse.builder()
//            .shipments(shipmentClient.get(getList(shipments)))
//            .pricing(pricingClient.get(getList(pricing)))
//            .track(trackClient.get(getList(track)))
//            .build();
    }

    private AggregationResponse setShipments(AggregationResponse response) {
        return response.setShipments(shipmentClient.get(getList(response.getShipmentsParam())));
    }

    private AggregationResponse setTrack(AggregationResponse response) {
        return response.setTrack(trackClient.get(getList(response.getTrackParam())));
    }

    private AggregationResponse setPricing(AggregationResponse response) {
        return response.setPricing(pricingClient.get(getList(response.getPricingParam())));
    }

    private static List<String> getList(String commaSeparatedValues) {
        return Optional.ofNullable(commaSeparatedValues)
            .map(values -> Arrays.stream(values.split(","))
                .distinct()
                .toList())
            .orElse(Collections.emptyList());
    }
}
