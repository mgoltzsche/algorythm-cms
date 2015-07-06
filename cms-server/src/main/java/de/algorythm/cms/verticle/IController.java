package de.algorythm.cms.verticle;

import org.vertx.java.core.http.HttpServerRequest;

/**
 * Created by max on 17.05.15.
 */
public interface IController {

    void run(HttpServerRequest request, String... requestParams);
}
