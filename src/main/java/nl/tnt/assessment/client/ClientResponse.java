package nl.tnt.assessment.client;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
public abstract class ClientResponse<T> {

    private final Map<String, T> result = new HashMap<>();

    //need this to serialize a Map "properly"
    @JsonAnySetter
    void set(String key, T value) {
        result.put(key, value);
    }

    public Map<String, T> getResult(List<String> keys){
        final Map<String, T> map = new HashMap<>();
        keys.forEach(key -> map.put(key, result.getOrDefault(key, null)));
        return map;
    }

}
