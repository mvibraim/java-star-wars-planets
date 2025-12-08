package com.example.starwarsplanets.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/")
public class Planets {

  @GetMapping("/hello")
  public Map<String, String> hello() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Hello from Spring Boot");
    response.put("status", "success");
    return response;
  }

}
