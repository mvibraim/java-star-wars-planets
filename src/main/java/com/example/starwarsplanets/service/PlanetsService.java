package com.example.starwarsplanets.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import com.example.starwarsplanets.dto.ResponsePlanetDTO;
import com.example.starwarsplanets.dto.RequestPlanetDTO;
import com.example.starwarsplanets.dto.PagedResponsePlanetDTO;
import com.example.starwarsplanets.entity.Planet;
import com.example.starwarsplanets.mapper.PlanetMapper;
import com.example.starwarsplanets.repository.PlanetsRepository;
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

  @Transactional
  @CacheEvict(value = "planets", allEntries = true)
  public ResponsePlanetDTO save(RequestPlanetDTO requestPlanetDTO) {
    Planet planet = planetMapper.toEntity(requestPlanetDTO);
    Planet savedPlanet = planetsRepository.save(planet);
    return planetMapper.toDTO(savedPlanet);
  }

  @Transactional
  @CacheEvict(value = "planets", allEntries = true)
  public boolean delete(UUID id) {
    if (planetsRepository.existsById(id)) {
      planetsRepository.deleteById(id);
      return true;
    }
    return false;
  }

  @Transactional(readOnly = true)
  public PagedResponsePlanetDTO getAll(Pageable pageable) {
    Page<Planet> planets = planetsRepository.findAll(pageable);
    List<ResponsePlanetDTO> dtos = planetMapper.toDTOList(planets.getContent());
    return new PagedResponsePlanetDTO(dtos, planets.getTotalElements(), planets.getTotalPages(),
        planets.getSize(), planets.getNumber(), planets.isFirst(), planets.isLast());
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "planets", key = "#id")
  public Optional<ResponsePlanetDTO> getById(UUID id) {
    return planetMapper.toOptionalDTO(planetsRepository.findById(id));
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "planets", key = "#name")
  public Optional<ResponsePlanetDTO> getByName(String name) {
    return planetMapper.toOptionalDTO(planetsRepository.findByName(name));
  }
}
