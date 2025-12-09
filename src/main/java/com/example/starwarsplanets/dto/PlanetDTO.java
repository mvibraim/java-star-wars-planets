package com.example.starwarsplanets.dto;

import jakarta.validation.constraints.NotBlank;

public class PlanetDTO {

  @NotBlank(message = "Name cannot be empty")
  private String name;

  @NotBlank(message = "Terrain cannot be empty")
  private String terrain;

  @NotBlank(message = "Climate cannot be empty")
  private String climate;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTerrain() {
    return terrain;
  }

  public void setTerrain(String terrain) {
    this.terrain = terrain;
  }

  public String getClimate() {
    return climate;
  }

  public void setClimate(String climate) {
    this.climate = climate;
  }
}
