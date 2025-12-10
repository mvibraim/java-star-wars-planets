package com.example.starwarsplanets.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import com.example.starwarsplanets.service.PlanetsCacheService;
import com.example.starwarsplanets.service.SwapiService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Component
public class CacheInitializer implements SmartInitializingSingleton {

  private final PlanetsCacheService planetsCacheService;
  private final SwapiService swapiService;

  public CacheInitializer(PlanetsCacheService planetsCacheService, SwapiService swapiService) {
    this.planetsCacheService = planetsCacheService;
    this.swapiService = swapiService;
  }

  private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);

  @Override
  public void afterSingletonsInstantiated() {
    swapiService.indexPlanetsMovieAppearances()
        .forEach(planetsCacheService::saveData);

    logger.info("CacheInitializer: Redis cache initialized and ready");
  }
}
