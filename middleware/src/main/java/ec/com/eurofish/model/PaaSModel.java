package ec.com.eurofish.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.vertx.mutiny.sqlclient.Row;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaaSModel {
    Integer id;
    String description;
    @JsonProperty("web_id")
    String webId;
    String ip;
    Integer port;
    boolean ssl;
    @JsonProperty("root_path")
    String rootPath;
    Integer timeout;
    @JsonProperty("body_as_header")
    boolean bodyAsHeader;
    Map<String, Object> body;
    @JsonProperty("business_one")
    boolean businessOne;
    String cookie;

    public static PaaSModel from(Row row) {
        return PaaSModel.builder()
                .id(row.getInteger("id"))
                .description(row.getString("description"))
                .webId(row.getString("web_id"))
                .ip(row.getString("ip"))
                .port(row.getInteger("port"))
                .ssl(row.getBoolean("ssl"))
                .rootPath(row.getString("root_path"))
                .timeout(row.getInteger("timeout"))
                .bodyAsHeader(row.getBoolean("body_as_header"))
                // .body(row.get)
                .businessOne(row.getBoolean("business_one"))
                .cookie(row.getString("cookie"))
                .build();
    }
}
