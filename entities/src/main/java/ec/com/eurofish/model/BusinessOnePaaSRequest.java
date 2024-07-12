package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.mutiny.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusinessOnePaaSRequest {
    String id;
    @JsonProperty("pg_id")
    public Integer pgId;
    public String description;
    public String ip;
    public Integer port;
    @JsonProperty("root_path")
    public String rootPath;
    @JsonProperty("login_body")
    public Map<String, Object> loginBody;
    public Integer timeout;

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
