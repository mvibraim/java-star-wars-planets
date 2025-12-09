package com.example.starwarsplanets.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import com.example.starwarsplanets.service.PlanetsCacheService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Component
public class CacheInitializer implements SmartInitializingSingleton {

  private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);

  private final PlanetsCacheService planetsCacheService;

  public CacheInitializer(PlanetsCacheService planetsCacheService) {
    this.planetsCacheService = planetsCacheService;
  }

  @Override
  public void afterSingletonsInstantiated() {
    planetsCacheService.saveData("name", "marcus");
    planetsCacheService.saveData("another_name", "leona");
    logger.info("CacheInitializer: All singletons have been instantiated! {}", planetsCacheService.retrieveData("another_name"));
  }
}
