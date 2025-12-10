package com.example.starwarsplanets.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ResponsePlanetDTO(String id, @NotBlank(message = "Name cannot be empty") String name,
    @NotBlank(message = "Terrain cannot be empty") String terrain,
    @NotBlank(message = "Climate cannot be empty") String climate, LocalDateTime createdAt,
    LocalDateTime updatedAt) {

  public ResponsePlanetDTO(String name, String terrain, String climate) {
    this(null, name, terrain, climate, null, null);
  }
}
