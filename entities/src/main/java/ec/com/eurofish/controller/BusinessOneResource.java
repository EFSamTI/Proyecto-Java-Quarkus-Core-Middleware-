package ec.com.eurofish.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ec.com.eurofish.model.BusinessOnePaaSRequest;
import ec.com.eurofish.model.BusinessOnePaaSResponse;
import ec.com.eurofish.model.PGPaaSModel;
import ec.com.eurofish.service.BusinessPaaSService;
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

@Path("business-one")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Authenticated
public class BusinessOneResource {

    @Inject
    PGService pg;

    @POST
    public Uni<Map<String, String>> save(BusinessOnePaaSRequest request) {
        return BusinessPaaSService.save(request)
                .onItem().call(bson -> {
                    request.setId(bson);
                    return pg.saveBusinessOnePaaS(request);
                })
                .onItem().transform(x -> Map.of("id", x));
    }

    @GET
    public Uni<List<BusinessOnePaaSResponse>> retrieveAll() {
        var pgList = pg.retrievePaaSList().collect().asList();
        var mongoList = BusinessPaaSService.all();

        return Uni.combine().all().unis(pgList, mongoList)
                .asTuple()
                .onItem().transform(tuple -> {
                    List<PGPaaSModel> list = tuple.getItem1();
                    List<BusinessPaaSService> mongo = tuple.getItem2();

                    var effective = mongo.stream().map(nonsql -> nonsql.id.toHexString()).toList();
                    var records = list.stream().filter(sql -> effective.contains(sql.getBsonid()))
                            .collect(Collectors.toList());

                    List<BusinessOnePaaSResponse> result = new ArrayList<>();
                    for (PGPaaSModel pgItem : records) {
                        var mongoItem = mongo.stream()
                                .filter(x -> x.id.toHexString().compareToIgnoreCase(pgItem.getBsonid()) == 0)
                                .collect(Collectors.toList());
                        if (!mongoItem.isEmpty()) {
                            var paas = mongoItem.get(0);
                            result.add(BusinessOnePaaSResponse.fromPG(pgItem)
                                    .serial(paas.id.toHexString())
                                    .ip(paas.ip)
                                    .port(paas.port)
                                    .rootPath(paas.rootPath)
                                    .loginBody(paas.loginBody)
                                    .timeout(paas.timeout)
                                    .build());
                        }
                    }

                    return result;
                });
    }

    @GET
    @Path("{bson}")
    public Uni<BusinessOnePaaSResponse> retrieveBySerial(@PathParam("bson") String bson) {
        var pgItem = pg.retrievePaaSByBson(bson);
        var mongoItem = BusinessPaaSService.bySerial(bson);

        return Multi.createBy()
                .combining()
                .streams(pgItem, mongoItem)
                .asTuple()
                .onItem().transform(tuple -> {
                    BusinessPaaSService paas = tuple.getItem2();

                    return BusinessOnePaaSResponse.fromPG(tuple.getItem1())
                            .serial(paas.id.toHexString())
                            .ip(paas.ip)
                            .port(paas.port)
                            .rootPath(paas.rootPath)
                            .loginBody(paas.loginBody)
                            .timeout(paas.timeout)
                            .build();
                }).toUni();
    }
}
