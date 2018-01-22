package com.mlc.url;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ImportResource;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

@SpringBootApplication
@ImportResource("url-shorten-beans.xml")
public class Application {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);

        UrlShortenHandler handler = ctx.getBean(UrlShortenHandler.class);

        Vertx vertx = Vertx.factory.vertx();
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.put("/shortenv").consumes("application/json").handler(handler);
        server.requestHandler(router::accept).listen(8080);
    }

}
