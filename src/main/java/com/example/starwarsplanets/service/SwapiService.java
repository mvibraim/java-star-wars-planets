package com.example.starwarsplanets.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.starwarsplanets.dto.SwapiPlanetDTO;
import com.example.starwarsplanets.dto.SwapiPlanetsResponseDTO;
import java.util.Map;
import java.util.HashMap;

@Service
public class SwapiService {

  private static final Logger logger = LoggerFactory.getLogger(SwapiService.class);
  private static final String SWAPI_BASE_URL = "https://swapi.dev/api";

  private final RestClient restClient;

  public SwapiService(RestClient.Builder restClientBuilder) {
    this.restClient = restClientBuilder.baseUrl(SWAPI_BASE_URL).build();
  }

  private List<SwapiPlanetDTO> fetchAllPlanets() {
    List<SwapiPlanetDTO> allPlanets = new ArrayList<>();
    String nextUrl = "/planets/";

    while (nextUrl != null) {
      logger.info("Fetching planets from: {}", nextUrl);
      SwapiPlanetsResponseDTO response =
          restClient.get().uri(nextUrl).retrieve().body(SwapiPlanetsResponseDTO.class);

      if (response != null && response.results() != null) {
        allPlanets.addAll(response.results());
        nextUrl = response.next() != null ? response.next().replace(SWAPI_BASE_URL, "") : null;
      } else {
        break;
      }
    }

    logger.info("Fetched {} planets from SWAPI", allPlanets.size());
    return allPlanets;
  }

  public Map<String, String> indexPlanetsMovieAppearances() {
    List<SwapiPlanetDTO> planets = fetchAllPlanets();
    Map<String, String> planetAppearances = new HashMap<>();

    for (SwapiPlanetDTO planet : planets) {
      planetAppearances.put(planet.name(), planet.films() != null ? String.valueOf(planet.films().size()) : "0");
    }

    return planetAppearances;
  }
}
