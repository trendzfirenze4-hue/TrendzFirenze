package com.mydev.ecommerce.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "API is running ✅";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }
}