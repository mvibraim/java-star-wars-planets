package com.example.starwarsplanets.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

@Component
public class CacheInitializer implements SmartInitializingSingleton {

  private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);

  @Override
  public void afterSingletonsInstantiated() {
    logger.info("CacheInitializer: Redis cache initialized and ready");
  }
}
