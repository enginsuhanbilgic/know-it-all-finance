package com.knowitall.myFmp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class MainPageController {

    @RequestMapping("/")
    public String redirectToMainPage(){
        return "redirect:/mainpage";
    }

    @RequestMapping("/mainpage")
    public String mainPage(){
        return "mainpage";
    }

}