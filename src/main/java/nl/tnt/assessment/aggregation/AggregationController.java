package nl.tnt.assessment.aggregation;

import nl.tnt.assessment.client.ShipmentClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentClient shipmentClient;

    public AggregationController(ShipmentClient shipmentClient) {
        this.shipmentClient = shipmentClient;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) {
        return AggregationResponse.builder()
            .shipments(shipmentClient.get(getList(shipments)))
            .build();
    }

    private static List<String> getList(String commaSeparatedValues) {
        return Optional.ofNullable(commaSeparatedValues)
            .map(values -> Arrays.stream(values.split(",")).toList())
            .orElse(Collections.emptyList());
    }
}
