package nl.tnt.assessment.aggregation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
@Builder
@Accessors(chain = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class AggregationResponse {

    @JsonIgnore
    private String shipmentsParam;
    @JsonIgnore
    private String pricingParam;
    @JsonIgnore
    private String trackParam;

    private Map<String, List<String>> shipments;
    private Map<String, Float> pricing;
    private Map<String, String> track;
}
