package com.example.starwarsplanets.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.starwarsplanets.services.PlanetsService;
import java.util.Map;

@RestController
@RequestMapping("/v1/")
public class PlanetsController {

  private final PlanetsService planetsService;

  public PlanetsController(PlanetsService planetsService) {
    this.planetsService = planetsService;
  }

  @GetMapping("/hello")
  public Map<String, String> hello() {
    return planetsService.getPlanet();
  }

}
