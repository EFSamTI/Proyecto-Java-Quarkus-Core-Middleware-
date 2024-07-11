package ec.com.eurofish.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ec.com.eurofish.model.BusinessOnePaaSRequest;
import ec.com.eurofish.model.BusinessOnePaaSResponse;
import ec.com.eurofish.model.PGPaaSModel;
import ec.com.eurofish.service.PGService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("business-one")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class BusinessOneResource {

    @Inject
    PGService pg;

    @POST
    public Uni<Response> save(BusinessOnePaaSRequest request) {
        return request.persistOrUpdate()
                .onItem().call(item -> {
                    request.id = ((BusinessOnePaaSRequest) item).id;
                    pg.saveBusinessOnePaaS(request);
                    return Uni.createFrom().item(item);
                })
                .onItem().transform(x -> Response.ok(x))
                .onItem().transform(ResponseBuilder::build);
    }

    @GET
    public Uni<Response> retrieveAll() {
        var pgList = pg.retrievePaaSList().collect().asList();
        var mongoList = BusinessOnePaaSRequest.all();

        return Uni.combine().all().unis(pgList, mongoList)
                .asTuple()
                .onItem().transform(tuple -> {
                    List<PGPaaSModel> list = tuple.getItem1();
                    List<BusinessOnePaaSRequest> mongo = tuple.getItem2();

                    var effective = mongo.stream().map(nonsql -> nonsql.id.toHexString()).toList();
                    var records = list.stream().filter(sql -> effective.contains(sql.getBsonid()))
                            .collect(Collectors.toList());

                    List<BusinessOnePaaSResponse> result = new ArrayList<>();
                    for (PGPaaSModel pgItem : records) {
                        var mongoItem = mongo.stream()
                                .filter(x -> x.id.toHexString().compareToIgnoreCase(pgItem.getBsonid()) == 0)
                                .collect(Collectors.toList());
                        if (!mongoItem.isEmpty()) {
                            var g = mongoItem.get(0);
                            result.add(BusinessOnePaaSResponse.fromPG(pgItem)
                                    .serial(g.id.toHexString())
                                    .ip(g.ip)
                                    .port(g.port)
                                    .rootPath(g.rootPath)
                                    .loginBody(g.loginBody)
                                    .timeout(g.timeout)
                                    .build());
                        }
                    }

                    return Uni.createFrom().item(result);
                })
                .onItem().transform(x -> Response.ok(x))
                .onItem().transform(ResponseBuilder::build);
    }
}
