package com.mlc.url;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

@RestController
public class UrlShortenController {

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

    @PutMapping(value = "/shorten", consumes = "application/json")
    public ResponseEntity<String> urlShorten(@RequestBody String body) {

        String longUrl = gson.fromJson(body, JsonElement.class).getAsJsonObject().get("longUrl").getAsString();

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

        return new ResponseEntity<String>(gson.toJson(urlShorten), HttpStatus.CREATED);
    }

    @GetMapping("/shorten")
    public ResponseEntity<String> getByShortUrl(@RequestParam(value = "shortUrl") String shortUrl) {
        String jsonList = gson.toJson(repo.findOne(shortUrl));
        return new ResponseEntity<String>(jsonList, HttpStatus.OK);
    }

    @GetMapping("/shorten/all")
    public ResponseEntity<String> getAll() {
        String jsonList = gson.toJson(repo.findAll());
        return new ResponseEntity<String>(jsonList, HttpStatus.OK);
    }

    @DeleteMapping("/shorten")
    public ResponseEntity<String> deleteByShortUrl(@RequestParam(value = "shortUrl") String shortUrl) {
        UrlShorten url = repo.findOne(shortUrl);
        repo.delete(url);
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @GetMapping("/shorten/go")
    public RedirectView goForShortUrl(@RequestParam(value = "shortUrl") String shortUrl) {
        UrlShorten url = repo.findOne(shortUrl);
        return new RedirectView(url.getLongUrl());
    }

}
