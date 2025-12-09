package com.example.starwarsplanets.services;

import org.springframework.stereotype.Service;
import com.example.starwarsplanets.repositories.PlanetsRepository;
import com.example.starwarsplanets.entities.Planet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetsService {

  private final PlanetsRepository planetsRepository;

  public PlanetsService(PlanetsRepository planetsRepository) {
    this.planetsRepository = planetsRepository;
  }

  public Planet save(Planet planet) {
    return planetsRepository.save(planet);
  }

  public boolean delete(UUID id) {
    if (planetsRepository.existsById(id)) {
      planetsRepository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }

  public List<Planet> getAll() {
    return planetsRepository.findAll();
  }

  public Optional<Planet> getById(UUID id) {
    return planetsRepository.findById(id);
  }
}
