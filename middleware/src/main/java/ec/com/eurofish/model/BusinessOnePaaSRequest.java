package ec.com.eurofish.model;

import java.net.URI;
import java.util.Map;

import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
// @MongoEntity(collection = "BusinessOne")
public class BusinessOnePaaSRequest {
    static final Logger log = Logger.getLogger(BusinessOnePaaSRequest.class);

    // @BsonId
    // public String id;
    ObjectId id;
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
    @BsonIgnore
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

    @BsonIgnore
    @JsonIgnore
    public String getHexId() {
        return id.toHexString();
    }

    public static BusinessOnePaaSRequest fromDocument(Document doc) {
        return BusinessOnePaaSRequest.builder()
                .id(doc.getObjectId("_id"))
                .ip(doc.getString("ip"))
                .port(doc.getInteger("port"))
                .rootPath(doc.getString("rootPath"))
                // .loginBody(doc.get("loginBody"))
                .timeout(doc.getInteger("timeout"))
                .cookie(doc.getString("cookie"))
                .build();
    }
    // public static BusinessOnePaaSRequest fromMongoItem(BusinessPaaSService item)
    // {
    // return BusinessOnePaaSRequest.builder()
    // .id(item.id.toHexString())
    // .ip(item.ip)
    // .port(item.port)
    // .rootPath(item.rootPath)
    // .loginBody(item.loginBody)
    // .timeout(item.timeout)
    // .cookie(item.cookie)
    // .build();
    // }

}
