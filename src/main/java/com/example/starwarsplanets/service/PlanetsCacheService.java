package com.example.starwarsplanets.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PlanetsCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public PlanetsCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveData(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object retrieveData(String key) {
        return redisTemplate.opsForValue().get(key);
    }
}
