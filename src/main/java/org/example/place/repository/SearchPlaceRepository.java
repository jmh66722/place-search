package org.example.place.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SearchPlaceRepository {

    private String KEY_SPACE = "SEARCH_PLACE:";
    @Autowired
    private RedisTemplate redisTemplate;

    public void increaseCount(String keyword) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String key = KEY_SPACE + keyword;
        String hashKey = "COUNT";

        hashOperations.increment(key, hashKey, 1);
    }

    public void cached(String keyword, String value) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        String key = KEY_SPACE + keyword;
        String hashKey = "RESULT";

        hashOperations.put(key, hashKey, value);
    }
}
