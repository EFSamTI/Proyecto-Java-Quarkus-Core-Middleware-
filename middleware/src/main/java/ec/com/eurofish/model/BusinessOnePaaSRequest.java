package ec.com.eurofish.model;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ec.com.eurofish.service.BusinessPaaSService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BusinessOnePaaSRequest {
    String id;
    @JsonProperty("pg_id")
    Integer pgId;
    String description;
    String ip;
    Integer port;
    @JsonProperty("root_path")
    String rootPath;
    @JsonProperty("login_body")
    Map<String, Object> loginBody;
    Integer timeout;
    @JsonIgnore
    String cookie;

    public URI createLoginURI() {
        return createURI("/Login");
    }

    public URI createURI(String path) {
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
    public String getLoginJsonBody() {
        String json = "{}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(loginBody);
            log.info(json);
        } catch (JsonProcessingException e) {
            log.error("LOGIN BODY ERROR", e);
        }
        return json;
    }

    public static BusinessOnePaaSRequest fromMongoItem(BusinessPaaSService item) {
        return BusinessOnePaaSRequest.builder()
                .id(item.id.toHexString())
                .ip(item.ip)
                .port(item.port)
                .rootPath(item.rootPath)
                .loginBody(item.loginBody)
                .timeout(item.timeout)
                .cookie(item.cookie)
                .build();
    }

}
