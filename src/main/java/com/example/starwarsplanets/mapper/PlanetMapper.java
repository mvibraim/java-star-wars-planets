package com.example.starwarsplanets.mapper;

import org.mapstruct.Mapper;
import com.example.starwarsplanets.dto.PlanetDTO;
import com.example.starwarsplanets.entities.Planet;
import org.mapstruct.Mapping;
import java.util.List;
import java.util.Optional;


@Mapper(componentModel = "spring")
public interface PlanetMapper {

  @Mapping(target = "id", ignore = true)
  Planet toEntity(PlanetDTO planetDTO);

  PlanetDTO toDTO(Planet planet);

  List<PlanetDTO> toDTOList(List<Planet> planets);

  default Optional<PlanetDTO> toOptionalDTO(Optional<Planet> sourceOptional) {
    return sourceOptional.map(this::toDTO);
  }

}
