package com.example.starwarsplanets.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.starwarsplanets.services.PlanetsService;
import com.example.starwarsplanets.entities.Planet;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/v1/")
public class PlanetsController {

  private final PlanetsService planetsService;

  public PlanetsController(PlanetsService planetsService) {
    this.planetsService = planetsService;
  }

  @PostMapping("/planets")
  public ResponseEntity<Planet> save(@RequestBody Planet planet) {
    Planet savedPlanet = planetsService.save(planet);
    URI location = URI.create("/planets/" + planet.getId());
    return ResponseEntity.created(location).body(savedPlanet);
  }

  @DeleteMapping("/planets/{id}")
  public ResponseEntity<String> delete(@PathVariable UUID id) {
    if (planetsService.delete(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/planets")
  public List<Planet> getAll() {
    return planetsService.getAll();
  }

  @GetMapping("/products/{id}")
  public ResponseEntity<Planet> getById(@PathVariable UUID id) {
    Optional<Planet> planet = planetsService.getById(id);
    return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }

}
