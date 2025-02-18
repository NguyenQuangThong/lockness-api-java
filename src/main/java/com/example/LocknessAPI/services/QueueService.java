package com.example.LocknessAPI.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class QueueService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void dispatch(String id, String queueName, int attempts, String backoffType, int delay) {
        try {
            Map<String, Object> jobData = Map.of(
                    "id", id,
                    "attempts", attempts,
                    "backoff", Map.of("type", backoffType, "delay", delay)
            );

            String jobJson = objectMapper.writeValueAsString(jobData);

            redisTemplate.opsForList().leftPush(queueName, jobJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing job data", e);
        }
    }
}
