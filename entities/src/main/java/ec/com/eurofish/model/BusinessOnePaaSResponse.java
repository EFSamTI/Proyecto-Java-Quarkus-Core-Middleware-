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
public class BusinessOnePaaSResponse {

    private String serial;
    private Integer id;
    private String description;
    private String ip;
    private Integer port;
    @JsonProperty("root_path")
    private String rootPath;
    @JsonProperty("login_body")
    @Builder.Default()
    private Map<String, Object> loginBody = new HashMap<>();
    private Integer timeout;

    public static BusinessOnePaaSResponseBuilder fromPG(PGPaaSModel pg) {
        return BusinessOnePaaSResponse.builder()
                .id(pg.getId())
                .description(pg.getDescription());
    }

}
