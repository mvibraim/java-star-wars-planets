package com.example.starwarsplanets.mapper;

import org.mapstruct.Mapper;
import com.example.starwarsplanets.dto.ResponsePlanetDTO;
import com.example.starwarsplanets.dto.RequestPlanetDTO;
import com.example.starwarsplanets.entity.Planet;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface PlanetMapper {

  default Planet toEntity(ResponsePlanetDTO responsePlanetDTO) {
    if (responsePlanetDTO == null) {
      return null;
    }

    return new Planet(responsePlanetDTO.name(), responsePlanetDTO.terrain(), responsePlanetDTO.climate());
  }

  default Planet toEntity(RequestPlanetDTO requestPlanetDTO) {
    if (requestPlanetDTO == null) {
      return null;
    }

    return new Planet(requestPlanetDTO.name(), requestPlanetDTO.terrain(), requestPlanetDTO.climate());
  }

  default ResponsePlanetDTO toDTO(Planet planet) {
    if (planet == null) {
      return null;
    }

    return new ResponsePlanetDTO(planet.getId().toString(), planet.getName(), planet.getTerrain(),
        planet.getClimate(), planet.getCreatedAt(), planet.getUpdatedAt());
  }

  List<ResponsePlanetDTO> toDTOList(List<Planet> planets);

  default Optional<ResponsePlanetDTO> toOptionalDTO(Optional<Planet> sourceOptional) {
    return sourceOptional.map(this::toDTO);
  }
}
