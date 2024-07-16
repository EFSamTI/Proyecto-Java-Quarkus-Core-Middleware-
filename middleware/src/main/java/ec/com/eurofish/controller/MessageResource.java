package ec.com.eurofish.controller;

import java.util.concurrent.CompletableFuture;

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
        CompletableFuture<GenericPaaSService> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> GenericPaaSService
                .bySerial(request.getDestination())
                .subscribe()
                .with(item -> future.complete(item)));

        return Uni.createFrom()
                .completionStage(future)
                // .ifNoItem().after(Duration.ofMillis(1000)).fail()
                .onItem().transform(paas -> generic.request(request, GenericPaaSRequest.fromMongoItem(paas)))
                .onItem().transform(body -> {
                    registry
                            .counter("message", "generic", "succeeded")
                            .increment();
                    return Response.ok(body);
                })
                .onItem().transform(ResponseBuilder::build);
    }

    @POST
    @Path("business-one")
    public Uni<Response> businessOneMessage(MessageRequest request) {
        CompletableFuture<BusinessPaaSService> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> BusinessPaaSService
                .bySerial(request.getDestination())
                .subscribe()
                .with(item -> future.complete(item)));
        return Uni.createFrom()
                .completionStage(future)
                .onItem().transform(paas -> business.request(request, BusinessOnePaaSRequest.fromMongoItem(paas)))
                .onItem().transform(body -> {
                    registry
                            .counter("message", "business-one", "succeeded")
                            .increment();
                    return Response.ok(body);
                })
                .onItem().transform(ResponseBuilder::build);
    }
}
