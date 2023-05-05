package nl.tnt.assessment.aggregation;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class AggregationResponse {

    private Map<String, List<String>> shipments;
    private Map<String, Float> pricing;
}
