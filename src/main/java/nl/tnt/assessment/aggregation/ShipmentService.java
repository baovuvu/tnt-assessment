package nl.tnt.assessment.aggregation;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class ShipmentService {

    private final Deque<ShipmentRequest> deque = new ArrayDeque<>();
    private final int MAX_QUEUE_SIZE = 5;

    public Map<String, List<String>> getResult(List<String> orderNumbers){
        final ShipmentRequest shipmentRequest = ShipmentRequest.create(orderNumbers);
        deque.add(shipmentRequest);
        checkDeque();
        return shipmentRequest.getResult();
    }
    private void checkDeque() {
        final List<String> orders = deque.stream()
            .flatMap(shipmentRequest -> shipmentRequest.getOrderNumbers().stream())
            .distinct()
            .collect(Collectors.toList());
        if (orders.size() >= MAX_QUEUE_SIZE) {
            // todo: check 2nd story-functionality! Here we process all requests in the queue if the cap is hit, why limit it to only 5??
            processDeque(orders);
        }
    }

    private void processDeque(List<String> orders) {
        final List<Shipment> shipments = temp(orders);
        deque.forEach(shipmentRequest -> shipmentRequest.complete(shipments));
        deque.clear();
    }

    private List<Shipment> temp(List<String> orderNumbers){
        return orderNumbers.stream()
                .map(this::temp)
                .collect(Collectors.toList());
    }

    private Shipment temp(String orderNumber) {
        if (orderNumber.equals("109347263")) return Shipment.builder().orderNumber(orderNumber).products(List.of("box", "pallet", "pallet")).build();
        if (orderNumber.equals("123456891")) return Shipment.builder().orderNumber(orderNumber).products(Collections.emptyList()).build();
        return Shipment.builder().orderNumber(orderNumber).products(List.of("envelope")).build();

    }

}
