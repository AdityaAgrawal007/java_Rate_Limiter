package com.cerberus.rateLimiter.store;

import com.cerberus.rateLimiter.algorithm.redis.RedisRateLimitAlgorithm;
import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.core.StateStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class RedisStateStore implements StateStore {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisRateLimitAlgorithm algorithm;
    private final DefaultRedisScript<List> script;

    public RedisStateStore(RedisTemplate<String, Object> redisTemplate,
                           RedisRateLimitAlgorithm algorithm) throws IOException {
        this.redisTemplate = redisTemplate;
        this.algorithm = algorithm;

        ClassPathResource resource = new ClassPathResource(algorithm.getScriptPath());
        String lua = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        this.script = new DefaultRedisScript<>();
        this.script.setScriptText(lua);
        this.script.setResultType(List.class);
    }

    @Override
    public RateLimitResult tryConsume(String clientKey) {
        List<Long> result = redisTemplate.execute(
                script,
                Collections.singletonList(clientKey),
                algorithm.getArgv(clientKey).toArray()
        );

        return new RateLimitResult(
                result.get(0) == 1,
                result.get(1),
                result.get(2)
        );
    }
}