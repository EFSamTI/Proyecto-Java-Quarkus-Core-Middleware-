package ec.com.eurofish.model;

import java.net.URI;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BusinessOneModel extends PaaSModel {
    static final Logger log = Logger.getLogger(BusinessOneModel.class);

    public URI createLoginURI() {
        return createURI("/Login");
    }

    public URI createURI(String path) {
        StringBuilder builder = new StringBuilder("https://");
        builder.append(ip);

        if (port != 80)
            builder.append(String.format(":%d", port));

        builder.append(rootPath);
        builder.append(path);
        String uriString = builder.toString();
        log.info(uriString);
        return URI.create(uriString);
    }

    @JsonIgnore
    public String getLoginJsonBody() {
        String json = "{}";
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writeValueAsString(body);
            log.info(json);
        } catch (JsonProcessingException e) {

        }
        return json;
    }

}
