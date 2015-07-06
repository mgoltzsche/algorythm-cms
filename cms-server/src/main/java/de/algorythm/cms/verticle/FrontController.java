package de.algorythm.cms.verticle;

import de.algorythm.cms.CmsFacade;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 17.05.15.
 */
public class FrontController implements IController {

    private final Map<Pattern, IController> controllerMap = new LinkedHashMap<>();

    public void registerController(String queryRegex, IController controller) {
        final Pattern queryPattern = Pattern.compile(queryRegex);

        controllerMap.put(queryPattern, controller);
    }

    @Override
    public void run(HttpServerRequest request, String... requestParams) {
        for (Map.Entry<Pattern, IController> entry : controllerMap.entrySet()) {
            final String path = request.absoluteURI().getPath();
            final Matcher matcher = entry.getKey().matcher(path);

            if (matcher.find()) {
                final String[] params = new String[matcher.groupCount()];

                for (int i = 0; i < params.length; i++) {
                    params[i] = matcher.group(i + 1);
                }

                entry.getValue().run(request, params);

                return;
            }
        }

        notFound(request.response());
    }

    private void notFound(HttpServerResponse resp) {
        resp.headers().set("Content-Type", "text/html; charset=UTF-8");
        resp.setStatusCode(404);
        resp.setStatusMessage("Not Found");
        sendErrorHtml(resp, "Not Found", "The requested resource does not exist.");
    }

    private void sendErrorHtml(HttpServerResponse resp, String title, String msg) {
        resp.end(new StringBuilder("<html><head><title>")
                .append(title).append(" - algorythm CMS</title></head><body><h1>")
                .append(title).append("</h1><p>")
                .append(msg).append("</p></body></html>").toString());
    }
}
