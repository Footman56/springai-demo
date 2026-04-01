package com.huochai.aimemory.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 *@author peilizhi 
 *@date 2026/3/22 11:48
 **/
@Service
public class RedisChatMemory {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PREFIX = "chat:memory:";

    public void add(String sessionId, String message) {
        redisTemplate.opsForList().rightPush(PREFIX + sessionId, message);
    }

    public List<String> get(String sessionId) {
        List<String> result = redisTemplate.opsForList()
                .range(PREFIX + sessionId, 0, -1);
        return result != null ? result : List.of();
    }

    /**
     * 检查会话是否有历史记录
     */
    public boolean hasHistory(String sessionId) {
        Long size = redisTemplate.opsForList().size(PREFIX + sessionId);
        return size != null && size > 0;
    }

    public void clear(String sessionId) {
        redisTemplate.delete(PREFIX + sessionId);
    }
}
