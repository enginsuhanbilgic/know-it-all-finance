package com.knowitall.myFmp.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.knowitall.myFmp.model.CryptoCurrency;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CryptoCurrencyRepository extends MongoRepository<CryptoCurrency, String> {
    List<JsonNode> findBySymbolContainingIgnoreCaseOrNameContainingIgnoreCase(String symbol, String name);
}
