package com.knowitall.myFmp.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.knowitall.myFmp.service.SearchService;

import reactor.core.publisher.Mono;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public Mono<JsonNode> getCryptoInfo(@RequestParam(value = "searchText", required = true) String searchText){
        return searchService.searchCryptoCurrencies(searchText.trim());
    }

    @GetMapping("/searchlist")
    public Mono<JsonNode> getAllMatchingCrypto(@RequestParam(value = "searchText", required = true) String searchText){
        return searchService.getAllMatching(searchText.trim());
    }

}
