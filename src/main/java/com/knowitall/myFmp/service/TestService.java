package com.knowitall.myFmp.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TestService {

    @Value("${fmp.api.key}")
    private String apiKey;

    private final WebClient webClient;

    public TestService(WebClient.Builder webClientBuilder, @Value("${fmp.api.key}") String apiKey) {
        this.webClient = webClientBuilder.baseUrl("https://financialmodelingprep.com/api/v3/").build();
    }

    public Mono<JsonNode> getCurrentDayCryptoPrice(String symbol) {
        String currentDateString = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return webClient.get()
                .uri("/historical-price-full/{symbol}?apikey={apiKey}&timeseries=1&from={date}&to={date}",
                        symbol, apiKey, currentDateString, currentDateString)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}