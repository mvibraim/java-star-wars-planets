package com.example.starwarsplanets.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.example.starwarsplanets.dto.ResponsePlanetDTO;
import com.example.starwarsplanets.dto.RequestPlanetDTO;
import com.example.starwarsplanets.dto.PagedResponsePlanetDTO;
import com.example.starwarsplanets.service.PlanetsService;
import com.example.starwarsplanets.error.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

@RestController
@RequestMapping("/v1/")
@Tag(name = "Planets", description = "REST API for managing Star Wars planets")
public class PlanetsController {

  private final PlanetsService planetsService;

  public PlanetsController(PlanetsService planetsService) {
    this.planetsService = planetsService;
  }

  @PostMapping("/planets")
  @Operation(summary = "Create a new planet",
      description = "Creates a new planet in the database with the provided details")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Planet created successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ResponsePlanetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid planet data (validation failed)",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "409", description = "Planet name already exists",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))})
  public ResponseEntity<ResponsePlanetDTO> save(@Valid @RequestBody RequestPlanetDTO planet) {
    ResponsePlanetDTO savedPlanet = planetsService.save(planet);
    URI location = URI.create("/planets/");
    return ResponseEntity.created(location).body(savedPlanet);
  }

  @DeleteMapping("/planets/{id}")
  @Operation(summary = "Delete a planet",
      description = "Deletes a planet from the database by its UUID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Planet deleted successfully",
          content = @Content()),
      @ApiResponse(responseCode = "404", description = "Planet not found",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))})
  public ResponseEntity<Void> delete(
      @PathVariable @Parameter(description = "UUID of the planet to delete",
          example = "123e4567-e89b-12d3-a456-426614174000") UUID id) {
    if (planetsService.delete(id)) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }

  @GetMapping("/planets")
  @Operation(summary = "Get all planets",
      description = "Retrieves all planets with pagination support. Results are sorted by planet name in ascending order.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Planets retrieved successfully",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = PagedResponsePlanetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))})
  public ResponseEntity<PagedResponsePlanetDTO> getAll(
      @RequestParam(defaultValue = "0") @Parameter(description = "Zero-indexed page number",
          example = "0") int page,
      @RequestParam(defaultValue = "20") @Parameter(description = "Page size (1-100 items)",
          example = "20") int size) {
    int validPage = Math.max(0, page);
    int validSize = Math.clamp(size, 1, 100);
    Pageable pageable = PageRequest.of(validPage, validSize, Sort.by("name").ascending());
    return ResponseEntity.ok(planetsService.getAll(pageable));
  }

  @GetMapping("/planets/search")
  @Operation(summary = "Search for a planet",
      description = "Searches for a planet by either UUID or name. At least one parameter must be provided.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Planet found",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ResponsePlanetDTO.class))),
      @ApiResponse(responseCode = "400", description = "Name parameter is empty",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))),
      @ApiResponse(responseCode = "404", description = "Planet not found",
          content = @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class)))})
  public ResponseEntity<ResponsePlanetDTO> getByParam(
      @RequestParam(required = false) @Parameter(description = "UUID of the planet",
          example = "123e4567-e89b-12d3-a456-426614174000") UUID id,
      @RequestParam(required = false) @Size(min = 1, message = "Name must not be empty") @Parameter(
          description = "Name of the planet", example = "Tatooine") String name) {
    if (id != null) {
      Optional<ResponsePlanetDTO> planet = planetsService.getById(id);
      return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    Optional<ResponsePlanetDTO> planet = planetsService.getByName(name);
    return planet.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
  }
}
