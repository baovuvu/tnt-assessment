package nl.tnt.assessment.aggregation;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
@AllArgsConstructor(access = AccessLevel.PRIVATE) // builder
public class Shipment {
    private String orderNumber;
    private List<String> products;
}
