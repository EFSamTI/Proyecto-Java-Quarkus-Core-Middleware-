package ec.com.eurofish.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@MongoEntity(collection = "BusinessOne")
public class BusinessOnePaaSRequest extends ReactivePanacheMongoEntity {
    @BsonIgnore
    public Integer pgId;
    @BsonIgnore
    public String description;
    public String ip;
    public Integer port;
    @JsonProperty("root_path")
    public String rootPath;
    @JsonProperty("login_body")
    public Map<String, Object> loginBody;
    public Integer timeout;
    @JsonIgnore
    public String cookie;

    // @BsonIgnore
    // @JsonProperty("id")
    // private Integer pgId;
    // @BsonIgnore
    // private String description;
    // private String ip;
    // private Integer port;
    // // @BsonProperty()
    // @JsonProperty("root_path")
    // private String rootPath;
    // @JsonProperty("login_body")
    // @Builder.Default()
    // private Map<String, Object> loginBody = new HashMap<>();
    // private Integer timeout;
    // @JsonIgnore
    // private String cookie;

    @BsonIgnore
    @JsonIgnore
    public Tuple getPGJsonBody() {
        String json = "{}";
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pgId);
            map.put("description", description);
            map.put("bsonid", id.toHexString());
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(map);
            log.info(json);
        } catch (JsonProcessingException e) {
            log.error("B1 JSON ERROR", e);
        }
        return Tuple.of(json);
    }

    public static Uni<BusinessOnePaaSRequest> updateCookie(String id, String cookie) {
        Uni<BusinessOnePaaSRequest> x = BusinessOnePaaSRequest.findById(new ObjectId(id));
        return x
                .onItem().transform(item -> {
                    item.cookie = cookie;
                    return item;
                }).call(r -> r.persistOrUpdate());
    }

    public static Uni<List<BusinessOnePaaSRequest>> all() {
        return BusinessOnePaaSRequest.streamAll()
                .onItem().transform(x -> (BusinessOnePaaSRequest) x).collect().asList();
    }

    public static Multi<BusinessOnePaaSRequest> bySerial(String bson) {
        return BusinessOnePaaSRequest.findById(new ObjectId(bson))
                .onItem().transform(x -> (BusinessOnePaaSRequest) x).toMulti();
    }

}
