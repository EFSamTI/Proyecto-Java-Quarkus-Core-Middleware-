package ec.com.eurofish.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import ec.com.eurofish.model.BusinessOnePaaSRequest;
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

    public static Uni<String> save(BusinessOnePaaSRequest request) {
        BusinessPaaSService entity = new BusinessPaaSService();
        if (request.getId() != null || !request.getId().isBlank())
            entity.id = new ObjectId(request.getId());
        entity.ip = request.getIp();
        entity.port = request.getPort();
        entity.rootPath = request.getRootPath();
        entity.loginBody = request.getLoginBody();
        entity.timeout = request.getTimeout();
        return entity.persistOrUpdate().replaceWith(entity.id.toHexString());
    }

    public static Uni<BusinessPaaSService> updateCookie(String id, String cookie) {
        Uni<BusinessPaaSService> x = BusinessPaaSService.findById(new ObjectId(id));
        return x
                .onItem().transform(item -> {
                    item.cookie = cookie;
                    return item;
                }).call(r -> r.persistOrUpdate());
    }

    public static Uni<List<BusinessPaaSService>> all() {
        return BusinessPaaSService.streamAll()
                .onItem().transform(x -> (BusinessPaaSService) x).collect().asList();
    }

    public static Multi<BusinessPaaSService> bySerial(String bson) {
        return BusinessPaaSService.findById(new ObjectId(bson))
                .onItem().transform(x -> (BusinessPaaSService) x).toMulti();
    }

}
