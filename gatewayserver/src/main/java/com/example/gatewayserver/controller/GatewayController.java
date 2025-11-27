package com.example.gatewayserver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GatewayController {

    @GetMapping("/home")
    public ResponseEntity<String> homePage() {
        return ResponseEntity.ok("Home page");
    }
}
