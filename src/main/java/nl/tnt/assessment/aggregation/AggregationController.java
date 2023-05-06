package nl.tnt.assessment.aggregation;

import nl.tnt.assessment.client.PricingClient;
import nl.tnt.assessment.client.ShipmentClient;
import nl.tnt.assessment.client.TrackClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentClient shipmentClient;
    private final PricingClient pricingClient;
    private final TrackClient trackClient;

    public AggregationController(ShipmentClient shipmentClient, PricingClient pricingClient, TrackClient trackClient) {
        this.shipmentClient = shipmentClient;
        this.pricingClient = pricingClient;
        this.trackClient = trackClient;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) {
//        final Map<String, AggregationResponse> = Map.of(shipments)
        return AggregationResponse.builder()
            .shipments(shipmentClient.get(getList(shipments)))
            .pricing(pricingClient.get(getList(pricing)))
            .track(trackClient.get(getList(track)))
            .build();
    }

    private AggregationResponse setShipments(String param){
        return AggregationResponse.builder().shipments(shipmentClient.get(getList(param))).build();
    }

    private static List<String> getList(String commaSeparatedValues) {
        return Optional.ofNullable(commaSeparatedValues)
            .map(values -> Arrays.stream(values.split(","))
//                .filter(StringUtils::hasText)
                .distinct()
                .toList())
            .orElse(Collections.emptyList());
    }
}
