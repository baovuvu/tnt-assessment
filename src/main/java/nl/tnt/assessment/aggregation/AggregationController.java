package nl.tnt.assessment.aggregation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentService shipmentService;

    public AggregationController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) {
        return AggregationResponse.builder()
            .shipments(shipmentService.getResult(getList(shipments)))
            .build();
    }

    private static List<String> getList(String commaSeparatedValues) {
        return Optional.ofNullable(commaSeparatedValues)
            .map(values -> Arrays.stream(values.split(",")).toList())
            .orElse(Collections.emptyList());
    }
}
