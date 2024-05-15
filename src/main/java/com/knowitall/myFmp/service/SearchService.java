package com.knowitall.myFmp.service;

import com.knowitall.myFmp.controller.SearchController;
import com.knowitall.myFmp.model.CryptoCurrency;
import com.knowitall.myFmp.repository.CryptoCurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final CryptoCurrencyRepository cryptoCurrencyRepository;

    @Autowired
    public SearchService(CryptoCurrencyRepository cryptoCurrencyRepository){
        this.cryptoCurrencyRepository = cryptoCurrencyRepository;
    }

    public List<CryptoCurrency> searchCryptoCurrencies(String input){
        List<CryptoCurrency> cryptoCurrencies = cryptoCurrencyRepository.findBySymbolOrName(input);

        return cryptoCurrencies;
    }

}
