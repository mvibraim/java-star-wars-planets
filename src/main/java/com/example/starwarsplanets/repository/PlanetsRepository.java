package com.example.starwarsplanets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.starwarsplanets.entity.Planet;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PlanetsRepository extends JpaRepository<Planet, UUID> {
  Optional<Planet> findByName(String name);
}
