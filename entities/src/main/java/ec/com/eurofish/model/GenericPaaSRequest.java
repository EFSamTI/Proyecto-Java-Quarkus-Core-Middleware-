package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.Map;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.mutiny.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenericPaaSRequest {
    static final Logger log = Logger.getLogger(GenericPaaSRequest.class);

    String id;
    @JsonProperty("pg_id")
    Integer pgId;
    String description;
    String ip;
    Integer port;
    boolean ssl;
    @JsonProperty("root_path")
    String rootPath;
    @JsonProperty("header")
    Map<String, String> header;
    Integer timeout;

    @JsonIgnore
    public Tuple getPGJsonBody() {
        String json = "{}";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pgId);
            map.put("description", description);
            map.put("bsonid", id);
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(map);
            log.info(json);
        } catch (JsonProcessingException e) {
            log.error("B1 JSON ERROR", e);
        }
        return Tuple.of(json);
    }

}
