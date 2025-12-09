package com.example.starwarsplanets.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.starwarsplanets.dto.PlanetDTO;
import com.example.starwarsplanets.service.PlanetsService;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/v1/")
public class PlanetsController {

  private final PlanetsService planetsService;

  public PlanetsController(PlanetsService planetsService) {
    this.planetsService = planetsService;
  }

  @PostMapping("/planets")
  public ResponseEntity<PlanetDTO> save(@Valid @RequestBody PlanetDTO planet) {
    PlanetDTO savedPlanet = planetsService.save(planet);
    URI location = URI.create("/planets/");
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
  public ResponseEntity<List<PlanetDTO>> getAll() {
    return new ResponseEntity<>(planetsService.getAll(), HttpStatus.OK);
  }

  @GetMapping("/planets/search")
  public ResponseEntity<PlanetDTO> getByParam(@RequestParam(required = false) UUID id,
      @RequestParam(required = false) @Size(min = 1,
          message = "Name must not be empty") String name) {
    if (id != null) {
      Optional<PlanetDTO> planet = planetsService.getById(id);
      return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    Optional<PlanetDTO> planet = planetsService.getByName(name);
    return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
