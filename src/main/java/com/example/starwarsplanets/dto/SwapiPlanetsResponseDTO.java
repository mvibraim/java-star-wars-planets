package com.example.starwarsplanets.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SwapiPlanetsResponseDTO(Long count, String next, String previous,
    @JsonProperty("results") List<SwapiPlanetDTO> results) {
}
