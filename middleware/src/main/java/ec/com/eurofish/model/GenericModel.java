package ec.com.eurofish.model;

import java.net.URI;

import org.jboss.logging.Logger;

public class GenericModel extends PaaSModel {
    static final Logger log = Logger.getLogger(GenericModel.class);

    public URI createURI(String path) {
        StringBuilder builder = new StringBuilder("http");
        if (ssl)
            builder.append("s");
        builder.append(String.format("://%s", ip));

        if (port != 80)
            builder.append(String.format(":%d", port));

        builder.append(rootPath);
        builder.append(path);
        String uriString = builder.toString();
        log.info(uriString);
        return URI.create(uriString);
    }

}
