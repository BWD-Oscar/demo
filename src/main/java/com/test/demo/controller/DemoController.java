package com.test.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
public class DemoController {

    private final RestClient restClient = RestClient.create();

    @GetMapping("/api/test")
    public String test(){
        return "test";
    }

    @GetMapping("/api/demo")
    public String demo(){
        return "demo";
    }

    @GetMapping("/api/external")
    public String external() throws Exception {
        java.net.http.HttpClient client =
                java.net.http.HttpClient.newHttpClient();

        java.net.http.HttpRequest request =
                java.net.http.HttpRequest.newBuilder()
                        .uri(java.net.URI.create(
                                "https://fake-json-api.mock.beeceptor.com/users"))
                        .GET()
                        .build();

        java.net.http.HttpResponse<String> response =
                client.send(
                        request,
                        java.net.http.HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
