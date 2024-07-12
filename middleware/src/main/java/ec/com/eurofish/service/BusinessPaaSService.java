package ec.com.eurofish.service;

import java.util.Map;

import org.bson.types.ObjectId;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "BusinessOne")
public class BusinessPaaSService extends PanacheMongoEntity {
    public String ip;
    public Integer port;
    public String rootPath;
    public Map<String, Object> loginBody;
    public Integer timeout;
    public String cookie;

    public static BusinessPaaSService updateCookie(String id, String cookie) {
        BusinessPaaSService x = BusinessPaaSService.findById(new ObjectId(id));
        x.cookie = cookie;
        x.persistOrUpdate();
        return x;
    }

    public static BusinessPaaSService bySerial(String bson) {
        return BusinessPaaSService.findById(new ObjectId(bson));
    }

}
