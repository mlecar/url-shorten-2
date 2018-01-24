package com.mlc.url;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

@SpringBootApplication
@ImportResource("url-shorten-beans.xml")
public class Application {

    @Autowired
    private HTTPServerVerticle httpServerVerticle;

    @Autowired
    private UrlShortenVerticle urlShortenVerticle;

    @Autowired
    private Vertx vertx;

    @Autowired
    private Router router;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        /*
         * UrlShortenHandler handler = ctx.getBean(UrlShortenHandler.class);
         * 
         * Vertx vertx = Vertx.factory.vertx(); Router router =
         * Router.router(vertx); HttpServer server = vertx.createHttpServer();
         * 
         * server.requestHandler(router::accept).listen(8080);
         * router.route().handler(BodyHandler.create());
         * router.put("/shortenv").consumes("application/json").handler(handler:
         * :handle);
         */
    }

    @PostConstruct
    public void deployVerticle() {
        router.route().handler(BodyHandler.create());
        Future<String> httpServerVerticleDeployment = Future.future();
        vertx.deployVerticle(httpServerVerticle, httpServerVerticleDeployment.completer());
        vertx.deployVerticle(urlShortenVerticle);
    }

}
