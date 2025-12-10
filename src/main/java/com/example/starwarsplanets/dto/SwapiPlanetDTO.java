package com.example.starwarsplanets.dto;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record SwapiPlanetDTO(String name, @JsonProperty("films") List<String> films) {
}
