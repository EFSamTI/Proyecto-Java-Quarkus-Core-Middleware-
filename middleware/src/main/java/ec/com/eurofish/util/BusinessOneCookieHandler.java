package ec.com.eurofish.util;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BusinessOneCookieHandler extends CookieHandler {

    final ConcurrentHashMap<String, List<String>> cookies;

    public BusinessOneCookieHandler(String session) {
        super();
        List<String> values = new ArrayList<>();
        values.add(session);
        this.cookies = new ConcurrentHashMap<>();
        cookies.put("Cookie", values);
    }

    @Override
    public Map<String, List<String>> get(URI uri, Map<String, List<String>> requestHeaders) throws IOException {
        return cookies;
    }

    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {

    }

}
