package com.mlc.url;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;

@Component
public class UrlShortenVerticle extends AbstractVerticle {

    private static Logger logger = LoggerFactory.getLogger(UrlShortenVerticle.class);

    @Autowired
    private UrlShortenHandler urlShortenHandler;

    @Autowired
    private Router router;

    @Override
    public void start() {
        router.put("/shorten").consumes("application/json").handler(urlShortenHandler::add);
        router.get("/shorten").handler(urlShortenHandler::getByShortUrl);
        router.get("/shorten/all").handler(urlShortenHandler::getAll);
        router.delete("/shorten").handler(urlShortenHandler::deleteByShortUrl);
        router.get("/shorten/go").handler(urlShortenHandler::goForShortUrl);
    }
}
