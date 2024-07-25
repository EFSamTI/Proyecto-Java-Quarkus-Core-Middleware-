package ec.com.eurofish.service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.jboss.logging.Logger;

import ec.com.eurofish.model.GenericPaaSRequest;
import ec.com.eurofish.model.MessageRequest;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GenericService {
    static final Logger log = Logger.getLogger(GenericService.class);

    public String request(MessageRequest message, GenericPaaSRequest paas) {
        var builder = HttpRequest.newBuilder()
                .uri(paas.createURI(message.getPath()))
                .timeout(java.time.Duration.ofMillis(paas.getTimeout()))
                .method(message.getVerb(), HttpRequest.BodyPublishers.ofString(message.getJsonBody()));
        try {
            paas.getHeader().forEach((k, v) -> builder.header(k, v));
        } catch (IllegalArgumentException e) {
            log.error("HTTP HEADER ERROR", e);
        }
        HttpRequest httpRequest = builder
                .build();
        // CookieHandler cookieHandler = new BusinessOneCookieHandler(paas.getCookie());
        HttpClient httpClient = HttpClient.newBuilder()
                .build();
        String body = null;
        try {
            // log.info(paas.toString());
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            body = httpResponse.body();
        } catch (IOException | InterruptedException e) {
            log.error("HTTP REQUEST ERROR", e);
        }
        return body;
    }
}
