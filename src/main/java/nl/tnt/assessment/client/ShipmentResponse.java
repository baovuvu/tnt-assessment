package nl.tnt.assessment.client;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
public class ShipmentResponse {

    private final Map<String, List<String>> shipments = new HashMap<>();

    //need this to serialize a Map "properly"
    @JsonAnySetter
    void setShipment(String key, List<String> value) {
        shipments.put(key, value);
    }
}
