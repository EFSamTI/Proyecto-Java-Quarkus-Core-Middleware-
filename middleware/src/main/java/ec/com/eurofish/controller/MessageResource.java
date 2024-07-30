package ec.com.eurofish.controller;

import java.time.Duration;

import ec.com.eurofish.model.MessageRequest;
import ec.com.eurofish.service.BusinessService;
import ec.com.eurofish.service.GenericService;
import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.handler.HttpException;
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

        @POST
        @Path("generic")
        public Uni<Response> genericMessage(MessageRequest request) {
                return Uni.createFrom()
                                .completionStage(business.find(request.getDestination()))
                                .ifNoItem().after(Duration.ofMillis(1000)).fail()
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
                                .completionStage(business.find(request.getDestination()))
                                .ifNoItem().after(Duration.ofMillis(1000))
                                .failWith(() -> new HttpException(500,
                                                "PaaS <%s> not found".formatted(request.getDestination())))
                                .onItem().transform(paas -> {
                                        if (paas.getCookie() == null) {
                                                String cookie = business.login(paas);
                                                paas = business.update(paas.getWebId(), cookie);
                                        }
                                        return paas;
                                })
                                .onItem()
                                .transform(paas -> // business.request(request, paas)
                                {
                                        try {
                                                return business.request(request, paas);
                                        } catch (HttpException httpEx) {
                                                String cookie = business.login(paas);
                                                business.update(paas.getWebId(), cookie);
                                                return null;
                                        }
                                })
                                .ifNoItem().after(Duration.ofMillis(3000))
                                .failWith(() -> new HttpException(401, "SAP B1 Service Layer Error"))
                                .onItem().transform(body -> {
                                        registry
                                                        .counter("message", "business-one", "succeeded")
                                                        .increment();
                                        return Response.ok(body);
                                })
                                .onItem().transform(ResponseBuilder::build);
        }
}
