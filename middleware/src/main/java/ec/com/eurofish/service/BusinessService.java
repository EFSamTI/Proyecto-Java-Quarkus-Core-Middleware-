package ec.com.eurofish.service;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.Socket;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509ExtendedTrustManager;

import org.jboss.logging.Logger;

import ec.com.eurofish.model.MessageRequest;
import ec.com.eurofish.model.PaaSModel;
import ec.com.eurofish.util.BusinessOneCookieHandler;
import io.vertx.ext.web.handler.HttpException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class BusinessService {
    static final Logger log = Logger.getLogger(BusinessService.class);

    @Inject
    PostgreSQLService postgres;

    private SSLContext context() throws NoSuchAlgorithmException, KeyManagementException {
        var trustManager = new X509ExtendedTrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, SSLEngine engine) {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType, Socket socket)
                    throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType, Socket socket)
                    throws CertificateException {
            }
        };
        var sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[] { trustManager }, new SecureRandom());
        return sslContext;
    }

    public String login(PaaSModel paas) {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(paas.createBusinessOneLoginURI())
                .timeout(java.time.Duration.ofMillis(paas.getTimeout()))
                .POST(HttpRequest.BodyPublishers.ofString(paas.getBusinessOneLoginJsonBody()))
                .build();
        HttpClient httpClient = HttpClient.newBuilder().build();
        try {
            httpClient = HttpClient.newBuilder()
                    .sslContext(context())
                    .build();
        } catch (Exception e) {
            log.error("SSL CONTEXT ERROR", e);
        }

        StringBuilder builder = new StringBuilder();
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            httpResponse.headers().allValues("set-cookie").forEach(v -> {
                builder.append(v.substring(0, v.indexOf(";") + 1));
            });
            return builder.toString();
        } catch (IOException | InterruptedException e) {
            log.error("HTTP REQUEST ERROR", e);
        }
        return null;
    }

    public String request(MessageRequest msgRequest, PaaSModel paas) throws HttpException {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(paas.createBusinessOneURI(msgRequest.getPath()))
                .timeout(java.time.Duration.ofMillis(paas.getTimeout()))
                .method(msgRequest.getVerb(), HttpRequest.BodyPublishers.ofString(msgRequest.getJsonBody()))
                .build();
        CookieHandler cookieHandler = new BusinessOneCookieHandler(paas.getCookie());
        HttpClient httpClient = HttpClient.newBuilder().build();
        try {
            httpClient = HttpClient.newBuilder()
                    .sslContext(context())
                    .cookieHandler(cookieHandler)
                    .build();
        } catch (Exception e) {
            log.error("SSL CONTEXT ERROR", e);
        }

        String body = null;
        try {
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
            if (httpResponse != null && httpResponse.statusCode() == 401)
                throw new HttpException(401);
            //
            body = httpResponse.body();
        } catch (IOException | InterruptedException e) {
            log.error("HTTP REQUEST ERROR", e);
        }
        return body;
    }

    public CompletableFuture<PaaSModel> find(String webId) {
        CompletableFuture<PaaSModel> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> postgres
                .retrievePaaSByWebId(webId)
                .subscribe()
                .with(item -> future.complete(item)));
        return future;
    }

    public PaaSModel retrieve(String webId) {
        CompletableFuture<PaaSModel> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> postgres
                .retrievePaaSByWebId(webId)
                .subscribe()
                .with(item -> future.complete(item)));
        return future.join();
    }

    public PaaSModel update(UUID webId, String cookie) {
        CompletableFuture<PaaSModel> future = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> postgres
                .updateCookie(webId, cookie)
                .subscribe()
                .with(item -> future.complete(item)));
        return future.join();
    }

}
