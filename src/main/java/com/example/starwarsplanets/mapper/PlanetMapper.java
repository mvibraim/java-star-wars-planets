package com.example.starwarsplanets.mapper;

import org.mapstruct.Mapper;
import com.example.starwarsplanets.dto.PlanetDTO;
import com.example.starwarsplanets.entity.Planet;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PlanetMapper {

  default Planet toEntity(PlanetDTO planetDTO) {
    if (planetDTO == null) {
      return null;
    }

    return new Planet(planetDTO.name(), planetDTO.terrain(), planetDTO.climate());
  }

  default PlanetDTO toDTO(Planet planet) {
    if (planet == null) {
      return null;
    }

    return new PlanetDTO(planet.getName(), planet.getTerrain(), planet.getClimate());
  }

  List<PlanetDTO> toDTOList(List<Planet> planets);

  default Optional<PlanetDTO> toOptionalDTO(Optional<Planet> sourceOptional) {
    return sourceOptional.map(this::toDTO);
  }
}
