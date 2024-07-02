package com.knowitall.myFmp.controller;
import com.fasterxml.jackson.databind.JsonNode;

import reactor.core.publisher.Mono;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.knowitall.myFmp.service.TestService;

@RestController
public class TestController {
    
    @Autowired
    private final TestService testService;

    public TestController(TestService testService){
        this.testService = testService;
    }

    @GetMapping("/test")
    public Mono<JsonNode> testHistoricalData() {
        return testService.getCurrentDayCryptoPrice("BTCUSD");
    }

}
