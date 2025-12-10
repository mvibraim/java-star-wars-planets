package com.example.starwarsplanets.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.example.starwarsplanets.entity.Planet;
import java.util.UUID;
import java.util.Optional;

@Repository
public interface PlanetsRepository extends JpaRepository<Planet, UUID> {

  @Query("SELECT p FROM Planet p WHERE p.name = ?1")
  Optional<Planet> findByName(String name);

  Page<Planet> findAll(Pageable pageable);
}
