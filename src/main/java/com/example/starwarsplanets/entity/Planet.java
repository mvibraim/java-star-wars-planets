package com.example.starwarsplanets.entity;

import java.util.UUID;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "planets")
public class Planet {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(nullable = false, unique = true)
  private String name;

  @Column(nullable = false)
  private String terrain;

  @Column(nullable = false)
  private String climate;

  @CreationTimestamp
  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(nullable = false)
  private LocalDateTime updatedAt;

  public Planet() {}

  public Planet(String name, String terrain, String climate) {
    this.name = name;
    this.terrain = terrain;
    this.climate = climate;
  }

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

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
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

  public void setTerrain(String terrain) {
    this.terrain = terrain;
  }

  @Override
  public String toString() {
    return "Planet{" + "id=" + id + ", name='" + name + '\'' + ", terrain='" + terrain + '\''
        + ", climate='" + climate + '\'' + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
        + '}';
  }
}
