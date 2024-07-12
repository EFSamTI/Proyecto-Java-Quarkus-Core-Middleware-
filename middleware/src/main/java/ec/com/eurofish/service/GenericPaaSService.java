package ec.com.eurofish.service;

import java.util.Map;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "Generic")
public class GenericPaaSService extends PanacheMongoEntity {
    public String ip;
    public Integer port;
    public boolean ssl;
    public String rootPath;
    public Map<String, String> header;
    public Integer timeout;

    public static GenericPaaSService bySerial(String bson) {
        return GenericPaaSService.findById(new ObjectId(bson));
    }
}
