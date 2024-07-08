package com.knowitall.myFmp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {
    
    @GetMapping("/")
    public String homePage(){
        return "mainpage";
    }
    
    @GetMapping("/search")
    public String searchPage(){
        return "search";
    }

    @GetMapping("/searchlist")
    public String searchAllPage(){
        return "searchlist";
    }

}
