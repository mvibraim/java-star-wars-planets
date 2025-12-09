package com.example.starwarsplanets.repositories;

import com.example.starwarsplanets.entities.Planet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PlanetsRepository extends JpaRepository<Planet, UUID> {
  Optional<Planet> findByName(String name);
}
