package nl.tnt.assessment.client;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // hibernate, serializer
public abstract class ClientResponse<T> {

    private final Map<String, T> result = new HashMap<>();

    //need this to serialize a Map "properly"
    @JsonAnySetter
    void set(String key, T value) {
        result.put(key, value);
    }

    public Map<String, T> getResult(List<String> orderNumbers){
        return result.entrySet().stream()
            .filter(entry -> orderNumbers.contains(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
