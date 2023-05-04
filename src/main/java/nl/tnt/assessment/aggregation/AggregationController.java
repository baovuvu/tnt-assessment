package nl.tnt.assessment.aggregation;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("aggregation")
public class AggregationController {

    private final ShipmentService shipmentService;

    public AggregationController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @GetMapping
    public AggregationResponse get(String shipments, String track, String pricing) throws ExecutionException, InterruptedException {
        return AggregationResponse.builder()
            .shipments(shipmentService.get(getList(shipments)).get())
            .build();
    }

    private static List<String> getList(String values) {
        return Optional.ofNullable(values)
            .map(v -> Arrays.stream(v.split(",")).toList())
            .orElse(Collections.emptyList());
    }
}
