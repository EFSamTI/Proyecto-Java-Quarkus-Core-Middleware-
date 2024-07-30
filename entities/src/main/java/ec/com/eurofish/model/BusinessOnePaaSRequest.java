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
public class BusinessOnePaaSRequest {
    static final Logger log = Logger.getLogger(BusinessOnePaaSRequest.class);

    Integer id;
    @JsonProperty("web_id")
    public String webId;
    public String description;
    public String ip;
    public Integer port;
    @JsonProperty("root_path")
    public String rootPath;
    // @JsonProperty("body_as_header")
    // public boolean bodyAsHeader;
    @JsonProperty("body")
    public Map<String, Object> body;
    public Integer timeout;

    @JsonIgnore
    public Tuple getPGJsonBody() {
        String json = "{}";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", id);
            map.put("description", description);
            map.put("web_id", webId);
            map.put("ip", ip);
            map.put("port", port);
            map.put("root_path", rootPath);
            map.put("timeout", timeout);
            map.put("business_one", true);
            map.put("body_as_header", false);
            map.put("body", body);
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(map);
            log.info(json);
        } catch (JsonProcessingException e) {
            log.error("B1 JSON ERROR", e);
        }
        return Tuple.of(json);
    }

}
