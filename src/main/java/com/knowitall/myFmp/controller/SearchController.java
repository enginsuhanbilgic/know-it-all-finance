package com.knowitall.myFmp.controller;

import com.knowitall.myFmp.model.CryptoCurrency;
import com.knowitall.myFmp.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService){
        this.searchService = searchService;
    }

    @RequestMapping("/search")
    public String searchPage(){
        return "search";
    }
    /*
    @PostMapping("/pushsearch")
    public String getSearch(@RequestParam("searchQuery") String searchQuery, Model model){
        List<CryptoCurrency> cryptoCurrencies = searchService.searchCryptoCurrencies(searchQuery);
        model.addAttribute("cryptoCurrencies", cryptoCurrencies);
        return "search";
    }
*/
    @PostMapping("/pushsearch")
    public String getSearch(@RequestParam("searchQuery") String searchQuery){
        System.out.println(searchQuery);
        return searchQuery;
    }
}
