package ec.com.eurofish.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ec.com.eurofish.model.GenericPaaSRequest;
import ec.com.eurofish.model.GenericPaaSResponse;
import ec.com.eurofish.model.PGPaaSModel;
import ec.com.eurofish.service.PGService;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("generic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class GenericPaaSResource {

    @Inject
    PGService pg;

    @POST
    public Uni<Response> save(GenericPaaSRequest request) {
        return request.persistOrUpdate()
                .onItem().call(item -> {
                    request.id = ((GenericPaaSRequest) item).id;
                    pg.saveGenericPaaS(request);
                    return Uni.createFrom().item(item);
                })
                .onItem().transform(x -> Response.ok(x))
                .onItem().transform(ResponseBuilder::build);
    }

    @GET
    public Uni<Response> retrieveAll() {
        var pgList = pg.retrievePaaSList().collect().asList();
        var mongoList = GenericPaaSRequest.all();

        return Uni.combine().all().unis(pgList, mongoList)
                .asTuple()
                .onItem().transform(tuple -> {
                    List<PGPaaSModel> list = tuple.getItem1();
                    List<GenericPaaSRequest> mongo = tuple.getItem2();

                    var effective = mongo.stream().map(nonsql -> nonsql.id.toHexString()).toList();
                    var records = list.stream().filter(sql -> effective.contains(sql.getBsonid()))
                            .collect(Collectors.toList());

                    List<GenericPaaSResponse> result = new ArrayList<>();
                    for (PGPaaSModel pgItem : records) {
                        var mongoItem = mongo.stream()
                                .filter(x -> x.id.toHexString().compareToIgnoreCase(pgItem.getBsonid()) == 0)
                                .collect(Collectors.toList());
                        if (!mongoItem.isEmpty()) {
                            var paas = mongoItem.get(0);
                            result.add(GenericPaaSResponse.fromPG(pgItem)
                                    .serial(paas.id.toHexString())
                                    .ip(paas.ip)
                                    .port(paas.port)
                                    .ssl(paas.ssl)
                                    .rootPath(paas.rootPath)
                                    .header(paas.header)
                                    .timeout(paas.timeout)
                                    .build());
                        }
                    }

                    return Uni.createFrom().item(result);
                })
                .onItem().transform(x -> Response.ok(x))
                .onItem().transform(ResponseBuilder::build);
    }

    @GET
    @Path("{bson}")
    public Uni<Response> retrieveBySerial(@PathParam("bson") String bson) {
        var pgItem = pg.retrievePaaSByBson(bson);
        var mongoItem = GenericPaaSRequest.bySerial(bson);

        return Multi.createBy()
                .combining()
                .streams(pgItem, mongoItem)
                .asTuple()
                .onItem().transform(tuple -> {
                    GenericPaaSRequest paas = tuple.getItem2();

                    return Uni.createFrom().item(
                            GenericPaaSResponse.fromPG(tuple.getItem1())
                                    .serial(paas.id.toHexString())
                                    .ip(paas.ip)
                                    .port(paas.port)
                                    .ssl(paas.ssl)
                                    .rootPath(paas.rootPath)
                                    .header(paas.header)
                                    .timeout(paas.timeout)
                                    .build());
                })
                .onItem().transform(x -> Response.ok(x))
                .onItem().transform(ResponseBuilder::build).toUni();
    }
}
