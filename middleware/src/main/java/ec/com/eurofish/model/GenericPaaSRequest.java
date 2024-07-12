package ec.com.eurofish.model;

import java.net.URI;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import ec.com.eurofish.service.GenericPaaSService;
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
public class GenericPaaSRequest {
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

    public URI createURI(String path) {
        StringBuilder builder = new StringBuilder("http");
        if (ssl)
            builder.append("s");
        builder.append(String.format("://%s", ip));

        if (port != 80)
            builder.append(String.format(":%d", port));

        builder.append(rootPath);
        builder.append(path);
        String uriString = builder.toString();
        log.info(uriString);
        return URI.create(uriString);
    }

    public static GenericPaaSRequest fromMongoItem(GenericPaaSService item) {
        return GenericPaaSRequest.builder()
                .id(item.id.toHexString())
                .ip(item.ip)
                .port(item.port)
                .ssl(item.ssl)
                .rootPath(item.rootPath)
                .header(item.header)
                .timeout(item.timeout)
                .build();
    }

}
