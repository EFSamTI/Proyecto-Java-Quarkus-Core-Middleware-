package ec.com.eurofish.service;

import java.util.Map;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;

@MongoEntity(collection = "Generic")
public class GenericPaaSService extends ReactivePanacheMongoEntity {
    public String ip;
    public Integer port;
    public boolean ssl;
    public String rootPath;
    public Map<String, String> header;
    public Integer timeout;

    // public static GenericPaaSService bySerial(String bson) {
    // return GenericPaaSService.findById(new ObjectId(bson));
    // }
    public static Uni<GenericPaaSService> bySerial(String bson) {
        return GenericPaaSService.findById(new ObjectId(bson))
                .onItem().transform(x -> (GenericPaaSService) x);
    }
}
