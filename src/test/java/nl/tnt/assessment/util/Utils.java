package nl.tnt.assessment.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.tnt.assessment.aggregation.AggregationController;
import nl.tnt.assessment.aggregation.AggregationResponse;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

public class Utils {

    public static AggregationResponse testObject(){
        return AggregationResponse.builder()
            .shipmentsParam("109347263,123456891")
            .pricingParam("NL,CN,NL")
            .trackParam("109347263,123456891")
            .shipments(Map.of("109347263", List.of("box", "envelope", "pallet"), "123456891", List.of("box")))
            .pricing(Map.of("NL", 1.234f, "CN", 2.345f))
            .track(Map.of("109347263", "COLLECTING", "123456891", "IN TRANSIT"))
            .build();
    }

    public static List<String> getList(String commaSeparatedValues) {
        return AggregationController.getList(commaSeparatedValues);
    }

    public static String convertToJson(Object toConvert) {
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(toConvert);
        } catch (JsonProcessingException e) {
            return toConvert.toString();
        }
    }

    public static <T> T setPrivateField(T obj, String fieldName, Object value) {
        return setPrivateField(obj, obj.getClass(), fieldName, value);
    }

    private static <T> T setPrivateField(T obj, Class<?> objClass, String fieldName, Object value) {
        final Field field;
        try {
            field = objClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (objClass.getSuperclass() == Object.class) {
                throw new RuntimeException(e);
            } else {
                return setPrivateField(obj, objClass.getSuperclass(), fieldName, value);
            }
        }
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

}
