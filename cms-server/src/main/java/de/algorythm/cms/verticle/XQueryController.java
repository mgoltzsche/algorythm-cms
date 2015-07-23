/*package de.algorythm.cms.verticle;

import de.algorythm.cms.CmsFacade;
import de.algorythm.cms.IHandler;
import org.basex.api.client.Session;
import org.basex.core.cmd.XQuery;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;

import java.io.IOException;

public class XQueryController implements IController {

    private final CmsFacade db;
    private final String xquery;
    private final String[] paramNames;

    public XQueryController(CmsFacade db, String xquery, String... paramNames) {
        this.db = db;
        this.xquery = xquery;
        this.paramNames = paramNames;
    }

    @Override
    public void run(final HttpServerRequest request, String... params) {
        final XQuery query = new XQuery(xquery);

        query.bind("path", request.absoluteURI().getPath());

        for (int i = 0; i < Math.min(paramNames.length, params.length); i++) {
            query.bind(paramNames[i], params[i]);
        }

        db.execute(query, new IHandler<String>() {
            @Override
            public void handle(String responseContent) {
                final HttpServerResponse resp = request.response();

                resp.headers().set("Content-Type", "text/html; charset=UTF-8");
                resp.setStatusCode(200);
                resp.setStatusMessage("OK");
                resp.end(responseContent);
            }
        });
    }
}*/
