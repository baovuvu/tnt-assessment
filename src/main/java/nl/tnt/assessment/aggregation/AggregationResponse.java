package nl.tnt.assessment.aggregation;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class AggregationResponse {

    private Map<String, List<String>> shipments;
    private Map<String, Float> pricing;
    private Map<String, String> track;
}
