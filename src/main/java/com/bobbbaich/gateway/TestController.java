package com.bobbbaich.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Value("${gateway.url.reward}")
    private String urlReward;

    @GetMapping
    public Mono<String> test() {
        WebClient client = WebClient.builder()
                .baseUrl(urlReward)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultUriVariables(Collections.singletonMap("url", urlReward))
                .build();

        Mono<String> aaa = client.get().uri("/api/reward/gifts").exchangeToMono(response -> {
            if (response.statusCode().equals(HttpStatus.OK)) {
                return response.bodyToMono(String.class);
            } else if (response.statusCode().is4xxClientError()) {
                return Mono.just("Error response");
            } else {
                return response.createException().flatMap(Mono::error);
            }
        });

        return aaa;
    }
}
