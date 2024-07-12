package ec.com.eurofish.controller;

import ec.com.eurofish.model.BusinessOnePaaSRequest;
import ec.com.eurofish.model.GenericPaaSRequest;
import ec.com.eurofish.model.MessageRequest;
import ec.com.eurofish.service.BusinessPaaSService;
import ec.com.eurofish.service.BusinessService;
import ec.com.eurofish.service.GenericPaaSService;
import ec.com.eurofish.service.GenericService;
import ec.com.eurofish.service.PGService;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;

@Path("message")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MessageResource {

    @Inject
    PGService pg;

    @Inject
    MeterRegistry registry;

    @Inject
    GenericService generic;

    @Inject
    BusinessService business;

    @POST
    @Path("generic")
    public Uni<Response> genericMessage(MessageRequest request) {
        GenericPaaSService paas = GenericPaaSService.bySerial(request.getDestination());
        return Uni.createFrom()
                .item(generic.request(request, GenericPaaSRequest.fromMongoItem(paas)))
                .onItem().transform(body -> {
                    registry
                            .counter("generic.execution", paas.id.toHexString(), "succeeded")
                            .increment();
                    return Response.ok(body);
                })
                .onItem().transform(ResponseBuilder::build);
    }

    @POST
    @Path("business-one")
    public Uni<Response> businessOneMessage(MessageRequest request) {
        BusinessPaaSService paas = BusinessPaaSService.bySerial(request.getDestination());
        return Uni.createFrom()
                .item(business.request(request, BusinessOnePaaSRequest.fromMongoItem(paas)))
                .onItem().transform(body -> {
                    registry
                            .counter("business.one.execution", paas.id.toHexString(), "succeeded")
                            .increment();
                    return Response.ok(body);
                })
                .onItem().transform(ResponseBuilder::build);
    }
}
