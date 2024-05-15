package com.knowitall.myFmp.repository;

import com.knowitall.myFmp.model.CryptoCurrency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoCurrencyRepository extends MongoRepository<CryptoCurrency, String> {
    List<CryptoCurrency> findBySymbolOrName(String input);
}
