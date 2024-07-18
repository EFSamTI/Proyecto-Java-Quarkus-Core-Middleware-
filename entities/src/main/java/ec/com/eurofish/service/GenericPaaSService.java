package ec.com.eurofish.service;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import ec.com.eurofish.model.GenericPaaSRequest;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

@MongoEntity(collection = "Generic")
public class GenericPaaSService extends ReactivePanacheMongoEntity {
    public String ip;
    public Integer port;
    public boolean ssl;
    public String rootPath;
    public Map<String, String> header;
    public Integer timeout;

    public static Uni<String> save(GenericPaaSRequest request) {
        GenericPaaSService entity = new GenericPaaSService();
        if (request.getId() != null && !request.getId().isBlank())
            entity.id = new ObjectId(request.getId());
        entity.ip = request.getIp();
        entity.port = request.getPort();
        entity.ssl = request.isSsl();
        entity.rootPath = request.getRootPath();
        entity.header = request.getHeader();
        entity.timeout = request.getTimeout();
        return entity.persistOrUpdate()
                .onItem().transform(item -> ((GenericPaaSService) item).id.toHexString());
        // .replaceWith(entity.id.toHexString());
    }

    public static Uni<List<GenericPaaSService>> all() {
        return GenericPaaSService.streamAll()
                .onItem().transform(x -> (GenericPaaSService) x).collect().asList();
    }

    public static Multi<GenericPaaSService> bySerial(String bson) {
        return GenericPaaSService.findById(new ObjectId(bson))
                .onItem().transform(x -> (GenericPaaSService) x).toMulti();
    }
}
