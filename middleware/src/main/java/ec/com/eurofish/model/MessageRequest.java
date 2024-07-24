package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonIgnore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    String source;
    String destination;
    String operation;
    String verb;
    String path;
    Map<String, Object> body = new HashMap<>();
    Feedback feedback;

    @BsonIgnore
    public String getJsonBody() {
        String json = "{}";
        if (body != null)
            try {
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(this.body);
                log.info(json);
            } catch (JsonProcessingException e) {
                log.error("ERROR MESSAGE REQUEST", e);
            }
        return json;
    }

}
