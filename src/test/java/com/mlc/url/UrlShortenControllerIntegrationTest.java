package com.mlc.url;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlShortenControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenRepository repo;

    @MockBean
    private RestTemplate restTemplate;

    private String baseContext = "/shorten";

    @Test
    public void getByShortUrl() throws Exception {
        when(repo.findOne(anyString())).thenReturn(new UrlShorten("a.long.url", "a.short.url"));
        this.mockMvc.perform(get(baseContext).param("shortUrl", "a.short.url")).andExpect(status().isOk()).andExpect(jsonPath("$.longUrl").value("a.long.url")).andExpect(jsonPath("$.shortUrl").value("a.short.url"));
    }

    @Test
    public void getAll() throws Exception {
        List<UrlShorten> list = new ArrayList<>();
        list.add(new UrlShorten("a.long.url", "a.short.url"));
        when(repo.findAll()).thenReturn(list);
        this.mockMvc.perform(get(baseContext + "/all")).andExpect(status().isOk()).andExpect(jsonPath("$[0].longUrl").value("a.long.url")).andExpect(jsonPath("$[0].shortUrl").value("a.short.url"));
    }

    @Test
    public void redirect() throws Exception {
        when(repo.findOne(anyString())).thenReturn(new UrlShorten("a.long.url", "a.short.url"));
        this.mockMvc.perform(get(baseContext + "/go").param("shortUrl", "a.short.url")).andExpect(status().isFound());
    }

    @Test
    public void deleting() throws Exception {
        when(repo.findOne(anyString())).thenReturn(new UrlShorten("a.long.url", "a.short.url"));
        this.mockMvc.perform(delete(baseContext).param("shortUrl", "a.short.url")).andExpect(status().isOk());
    }

    @Test
    public void add() throws Exception {
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>("{\"kind\": \"urlshortener#url\", \"id\": \"a.short.url\", \"longUrl\": \"a.long.url\"}", HttpStatus.OK));
        this.mockMvc.perform(put(baseContext).contentType(MediaType.APPLICATION_JSON).content("{\"longUrl\":\"a.long.url\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.longUrl").value("a.long.url"))
                .andExpect(jsonPath("$.shortUrl").value("a.short.url"));
    }

    @Test
    public void existsLongUrl() throws Exception {
        when(restTemplate.exchange(any(URI.class), any(HttpMethod.class), any(HttpEntity.class), eq(String.class)))
                .thenReturn(new ResponseEntity<String>("{\"kind\": \"urlshortener#url\", \"id\": \"a.short.url\", \"longUrl\": \"a.long.url\"}", HttpStatus.OK));
        when(repo.findOne(anyString())).thenReturn(new UrlShorten("a.long.url", "a.short.url"));
        this.mockMvc.perform(put(baseContext).contentType(MediaType.APPLICATION_JSON).content("{\"longUrl\":\"a.long.url\"}")).andExpect(status().isCreated()).andExpect(jsonPath("$.longUrl").value("a.long.url"))
                .andExpect(jsonPath("$.shortUrl").value("a.short.url"));
    }

}
