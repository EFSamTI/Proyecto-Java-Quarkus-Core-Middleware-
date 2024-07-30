package ec.com.eurofish.model;

import java.net.URI;
import java.util.UUID;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.sqlclient.Row;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaaSModel {
    static final Logger log = Logger.getLogger(PaaSModel.class);

    Integer id;
    String description;
    @JsonProperty("web_id")
    UUID webId;
    String ip;
    Integer port;
    boolean ssl;
    @JsonProperty("root_path")
    String rootPath;
    Integer timeout;
    @JsonProperty("body_as_header")
    boolean bodyAsHeader;
    // Map<String, Object>
    JsonObject body;
    @JsonProperty("business_one")
    boolean businessOne;
    String cookie;

    public static PaaSModel from(Row row) {
        return PaaSModel.builder()
                .id(row.getInteger("id"))
                .description(row.getString("description"))
                .webId(row.getUUID("web_id"))
                .ip(row.getString("ip"))
                .port(row.getInteger("port"))
                .ssl(row.getBoolean("ssl"))
                .rootPath(row.getString("root_path"))
                .timeout(row.getInteger("timeout"))
                .bodyAsHeader(row.getBoolean("body_as_header"))
                .body(row.getJsonObject("body"))
                .businessOne(row.getBoolean("business_one"))
                .cookie(row.getString("cookie"))
                .build();
    }

    public URI createBusinessOneLoginURI() {
        return createBusinessOneURI("/Login");
    }

    public URI createBusinessOneURI(String path) {
        StringBuilder builder = new StringBuilder("https://");
        builder.append(ip);

        if (port != 80)
            builder.append(String.format(":%d", port));

        builder.append(rootPath);
        builder.append(path);
        String uriString = builder.toString();
        log.info(uriString);
        return URI.create(uriString);
    }

    @JsonIgnore
    public String getBusinessOneLoginJsonBody() {
        String json = "{}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(body);
            log.info(json);
        } catch (JsonProcessingException e) {

        }
        return json;
    }

}
