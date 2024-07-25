package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    static final Logger log = Logger.getLogger(MessageRequest.class);

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
