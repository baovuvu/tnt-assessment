package nl.tnt.assessment.aggregation;

import nl.tnt.assessment.client.PricingClient;
import nl.tnt.assessment.client.ShipmentClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentClient shipmentClient;
    private final PricingClient pricingClient;

    public AggregationController(ShipmentClient shipmentClient, PricingClient pricingClient) {
        this.shipmentClient = shipmentClient;
        this.pricingClient = pricingClient;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) {
        return AggregationResponse.builder()
            .shipments(shipmentClient.get(getList(shipments)))
            .pricing(pricingClient.get(getList(pricing)))
            .build();
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
