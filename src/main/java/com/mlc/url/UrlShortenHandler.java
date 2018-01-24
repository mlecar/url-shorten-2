package com.mlc.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

@Component
public class UrlShortenHandler {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gson;

    @Autowired
    private UrlShortenRepository repo;

    @Value("${google.api.url}")
    private String googleApiUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

    public void add(RoutingContext rc) {

        String longUrl = gson.fromJson(rc.getBodyAsString(), JsonElement.class).getAsJsonObject().get("longUrl").getAsString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(googleApiUrl).queryParam("key", googleApiKey);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("longUrl", longUrl);
        String param = gson.toJson(map);
        HttpEntity<String> entity = new HttpEntity<>(param, headers);

        ResponseEntity<String> response = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, entity, String.class);

        JsonElement e = gson.fromJson(response.getBody(), JsonElement.class);
        UrlShorten url = repo.findOne(e.getAsJsonObject().get("id").getAsString());

        UrlShorten urlShorten = new UrlShorten(e.getAsJsonObject().get("longUrl").getAsString(), e.getAsJsonObject().get("id").getAsString());
        if (url == null) {
            repo.save(urlShorten);
        }

        response(rc.response(), HttpResponseStatus.CREATED, gson.toJson(urlShorten));
    }

    public void getByShortUrl(RoutingContext rc) {
        String jsonList = gson.toJson(repo.findOne(rc.request().getParam("shortUrl")));
        response(rc.response(), HttpResponseStatus.OK, jsonList);
    }

    public void getAll(RoutingContext rc) {
        String jsonList = gson.toJson(repo.findAll());
        response(rc.response(), HttpResponseStatus.OK, jsonList);
    }

    public void deleteByShortUrl(RoutingContext rc) {
        UrlShorten url = repo.findOne(rc.request().getParam("shortUrl"));
        repo.delete(url);
        response(rc.response(), HttpResponseStatus.OK);
    }

    public void goForShortUrl(RoutingContext rc) {
        UrlShorten url = repo.findOne(rc.request().getParam("shortUrl"));

        HttpServerResponse httpResponse = rc.response();
        httpResponse.putHeader("content-type", "application/json");
        httpResponse.setChunked(true);
        httpResponse.setStatusCode(HttpResponseStatus.FOUND.code());
        httpResponse.putHeader("location", url.getLongUrl());
        httpResponse.end();
    }

    private void response(HttpServerResponse httpResponse, HttpResponseStatus status, String response) {
        httpResponse.putHeader("content-type", "application/json");
        httpResponse.setChunked(true);
        httpResponse.setStatusCode(status.code());
        httpResponse.write(response);
        httpResponse.end();
    }

    private void response(HttpServerResponse httpResponse, HttpResponseStatus status) {
        httpResponse.putHeader("content-type", "application/json");
        httpResponse.setChunked(true);
        httpResponse.setStatusCode(status.code());
        httpResponse.end();
    }

}
