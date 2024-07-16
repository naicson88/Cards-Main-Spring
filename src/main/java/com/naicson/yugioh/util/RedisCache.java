package com.naicson.yugioh.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RedisCache {

    private static final String CACHE_KEY_PREFIX = "myCache:";
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public String getCachedData(String input) {
        String cacheKey = CACHE_KEY_PREFIX + input;

        String cachedData =  redisTemplate.opsForValue().get(cacheKey);

        if(cachedData == null){
            System.out.println("Result do if: ");
            String  result =  "Teste cache " + input;

            redisTemplate.opsForValue().set(cacheKey, result, 60, TimeUnit.SECONDS);

            return result;
        }

        return cachedData;
    }
}
