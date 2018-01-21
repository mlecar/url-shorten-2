package com.mlc.url;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UrlShorten {

    @Id
    private String shortUrl;
    private String longUrl;

    protected UrlShorten() {
    }

    public UrlShorten(String longUrl, String shortUrl) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    @Override
    public String toString() {
        return "UrlShorten [shortUrl=" + shortUrl + ", longUrl=" + longUrl + "]";
    }

}
