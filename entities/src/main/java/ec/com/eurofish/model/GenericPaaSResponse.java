package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
// @NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GenericPaaSResponse {

    private String serial;
    private Integer id;
    private String description;
    private String ip;
    private Integer port;
    boolean ssl;
    @JsonProperty("root_path")
    private String rootPath;
    @JsonProperty("header")
    @Builder.Default()
    private Map<String, String> header = new HashMap<>();
    private Integer timeout;

    public static GenericPaaSResponseBuilder fromPG(PGPaaSModel pg) {
        return GenericPaaSResponse.builder()
                .id(pg.getId())
                .description(pg.getDescription());
    }

}
