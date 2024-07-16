package ec.com.eurofish.service;

import java.util.Map;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@MongoEntity(collection = "BusinessOne")
public class BusinessPaaSService extends ReactivePanacheMongoEntity {
    public String ip;
    public Integer port;
    public String rootPath;
    public Map<String, Object> loginBody;
    public Integer timeout;
    public String cookie;

    public static Uni<BusinessPaaSService> updateCookie(String id, String cookie) {
        Uni<BusinessPaaSService> x = BusinessPaaSService.findById(new ObjectId(id));
        return x
                .onItem().transform(item -> {
                    item.cookie = cookie;
                    return item;
                }).call(r -> r.persistOrUpdate());
    }

    public static Multi<BusinessPaaSService> bySerial(String bson) {
        return BusinessPaaSService.findById(new ObjectId(bson))
                .onItem().transform(x -> (BusinessPaaSService) x).toMulti();
    }

}
