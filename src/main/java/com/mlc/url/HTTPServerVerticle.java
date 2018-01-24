package com.mlc.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

@Component
public class HTTPServerVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(HTTPServerVerticle.class);

    @Autowired
    private Router router;

    @Autowired
    private Vertx vertx;

    @Override
    public void start(Future<Void> future) {
        HttpServer server = vertx.createHttpServer();
        server.requestHandler(router::accept).listen(8080, ar -> {
            if (ar.succeeded()) {
                logger.info("HTTP server running on port 8080");
                future.complete();
            } else {
                logger.error("Could not start a HTTP server", ar.cause());
                future.fail(ar.cause());
            }
        });
    }
}
