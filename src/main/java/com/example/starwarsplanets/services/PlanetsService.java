package com.example.starwarsplanets.services;

import org.springframework.stereotype.Service;
import com.example.starwarsplanets.repositories.PlanetsRepository;
import com.example.starwarsplanets.entities.Planet;

@Service
public class PlanetsService {

  private final PlanetsRepository planetsRepository;

  public PlanetsService(PlanetsRepository planetsRepository) {
    this.planetsRepository = planetsRepository;
  }

  public Planet save(Planet planet) {
    return planetsRepository.save(planet);
  }

}
