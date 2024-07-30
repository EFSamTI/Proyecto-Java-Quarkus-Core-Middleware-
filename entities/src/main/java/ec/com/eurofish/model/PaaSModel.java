package ec.com.eurofish.model;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

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
}
