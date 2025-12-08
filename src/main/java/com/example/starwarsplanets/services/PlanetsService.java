package com.example.starwarsplanets.services;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class PlanetsService {

  public Map<String, String> getPlanet() {
    Map<String, String> response = new HashMap<>();
    response.put("message", "Hello from Spring Boot 4");
    response.put("status", "success");
    return response;
  }

}
