package com.example.starwarsplanets.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.starwarsplanets.services.PlanetsService;
import com.example.starwarsplanets.entities.Planet;

@RestController
@RequestMapping("/v1/")
public class PlanetsController {

  private final PlanetsService planetsService;

  public PlanetsController(PlanetsService planetsService) {
    this.planetsService = planetsService;
  }

  @PostMapping("/planets")
  public Planet savePlanet(@RequestBody Planet planet) {
    return planetsService.save(planet);
  }

}
