package com.example.starwarsplanets.dto;

import jakarta.validation.constraints.NotBlank;

public record PlanetDTO(@NotBlank(message = "Name cannot be empty") String name,
    @NotBlank(message = "Terrain cannot be empty") String terrain,
    @NotBlank(message = "Climate cannot be empty") String climate) {
}
