package ec.com.eurofish.controller;

import java.util.concurrent.CompletableFuture;

import ec.com.eurofish.model.BusinessOneModel;
import ec.com.eurofish.model.GenericModel;
import ec.com.eurofish.model.MessageRequest;
import ec.com.eurofish.service.BusinessService;
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
        MeterRegistry registry;

        @Inject
        GenericService generic;

        @Inject
        BusinessService business;

        @Inject
        PGService postgres;

        private CompletableFuture<GenericModel> findGeneric(String webId) {
                CompletableFuture<GenericModel> future = new CompletableFuture<>();
                CompletableFuture.runAsync(() -> postgres
                                .retrievePaaSByWebId(webId)
                                .subscribe()
                                .with(item -> future.complete((GenericModel) item)));
                return future;
        }

        private CompletableFuture<BusinessOneModel> findBusinessOne(String webId) {
                CompletableFuture<BusinessOneModel> future = new CompletableFuture<>();
                CompletableFuture.runAsync(() -> postgres
                                .retrievePaaSByWebId(webId)
                                .subscribe()
                                .with(item -> future.complete((BusinessOneModel) item)));
                return future;
        }

        @POST
        @Path("generic")
        public Uni<Response> genericMessage(MessageRequest request) {
                return Uni.createFrom()
                                .completionStage(findGeneric(request.getDestination()))
                                // .ifNoItem().after(Duration.ofMillis(1000)).fail()
                                .onItem()
                                .transform(paas -> generic.request(request, paas))
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
                return Uni.createFrom()
                                .completionStage(findBusinessOne(request.getDestination()))
                                // .item(business.bySerial(request.getDestination()))
                                .onItem()
                                .transform(paas -> business.request(request, paas))
                                .onItem().transform(body -> {
                                        registry
                                                        .counter("message", "business-one", "succeeded")
                                                        .increment();
                                        return Response.ok(body);
                                })
                                .onItem().transform(ResponseBuilder::build);
        }
}
