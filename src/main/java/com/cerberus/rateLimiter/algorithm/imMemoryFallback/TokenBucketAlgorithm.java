package com.cerberus.rateLimiter.algorithm.imMemoryFallback;

import com.cerberus.rateLimiter.algorithm.BucketState;
import com.cerberus.rateLimiter.algorithm.RateLimitAlgorithm;
import com.cerberus.rateLimiter.core.RateLimitResult;

import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketAlgorithm implements RateLimitAlgorithm {
    private final int tokensPerSecond;
    private final long maxTokens;
    private final ConcurrentHashMap<String, BucketState> map = new ConcurrentHashMap<>();

    public TokenBucketAlgorithm(int tokensPerSecond, long maxTokens) {
        this.tokensPerSecond = tokensPerSecond;
        this.maxTokens = maxTokens;
    }

    public RateLimitResult tryConsume(String clientKey) {
        RateLimitResult[] holder = new RateLimitResult[1];

        map.compute(clientKey, (key, existingValue) -> {
            long currentTime = System.currentTimeMillis() / 1000;

            // New client
            if (existingValue == null) {
                BucketState newClient = new BucketState(currentTime, maxTokens - 1);
                long resetTimestamp = currentTime + 1;
                holder[0] = new RateLimitResult(true, resetTimestamp, maxTokens - 1);
                return newClient;
            }

            // Calculate tokens to add based on time passed
            long timePassed = currentTime - existingValue.lastRefillTimestamp();
            long tokensToAdd = timePassed * tokensPerSecond;
            long currentTokens = Math.min(maxTokens, existingValue.tokens() + tokensToAdd);

            // Not enough tokens
            if (currentTokens < 1) {
                long resetTimestamp = existingValue.lastRefillTimestamp() + 1;
                holder[0] = new RateLimitResult(false, resetTimestamp, 0);
                return existingValue;
            }

            // Consume 1 token
            BucketState updated = new BucketState(currentTime, currentTokens - 1);
            long resetTimestamp = currentTime + 1;
            holder[0] = new RateLimitResult(true, resetTimestamp, currentTokens - 1);
            return updated;
        });

        return holder[0];
    }
}