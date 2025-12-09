package com.example.starwarsplanets.services;

import org.springframework.stereotype.Service;
import com.example.starwarsplanets.repositories.PlanetsRepository;
import com.example.starwarsplanets.dto.PlanetDTO;
import com.example.starwarsplanets.mapper.PlanetMapper;
import com.example.starwarsplanets.entities.Planet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PlanetsService {

  private final PlanetMapper planetMapper;
  private final PlanetsRepository planetsRepository;

  public PlanetsService(PlanetsRepository planetsRepository, PlanetMapper planetMapper) {
    this.planetsRepository = planetsRepository;
    this.planetMapper = planetMapper;
  }

  public PlanetDTO save(PlanetDTO planetDTO) {
    Planet planet = planetMapper.toEntity(planetDTO);
    Planet savedPlanet = planetsRepository.save(planet);
    return planetMapper.toDTO(savedPlanet);
  }

  public boolean delete(UUID id) {
    if (planetsRepository.existsById(id)) {
      planetsRepository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }

  public List<PlanetDTO> getAll() {
    return planetMapper.toDTOList(planetsRepository.findAll());
  }

  public Optional<PlanetDTO> getById(UUID id) {
    return planetMapper.toOptionalDTO(planetsRepository.findById(id));
  }

  public Optional<PlanetDTO> getByName(String name) {
    return planetMapper.toOptionalDTO(planetsRepository.findByName(name));
  }
}
