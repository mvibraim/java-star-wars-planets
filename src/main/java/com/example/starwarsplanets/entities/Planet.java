package com.example.starwarsplanets.entities;

import java.util.UUID;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "planets")
public class Planet {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String terrain;

  @Column(nullable = false)
  private String climate;

  // Constructors, getters and setters, and other methods...

  // Getters
  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getTerrain() {
    return terrain;
  }

  public String getClimate() {
    return climate;
  }

  // Setters
  public void setId(UUID id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setClimate(String climate) {
    this.climate = climate;
  }

  public void setQuantity(String terrain) {
    this.terrain = terrain;
  }
}
