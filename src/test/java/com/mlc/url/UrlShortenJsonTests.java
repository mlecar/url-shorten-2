package com.mlc.url;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class UrlShortenJsonTests {

    @Test
    public void serializeJson() throws IOException {
        UrlShorten url = new UrlShorten("a.long.url", "a.short.url");
        JsonElement e = new Gson().toJsonTree(url);

        assertThat(e.getAsJsonObject().get("longUrl").getAsString(), is("a.long.url"));
        assertThat(e.getAsJsonObject().get("shortUrl").getAsString(), is("a.short.url"));
    }

}
