package com.cerberus.rateLimiter.algorithm.redis;

import com.cerberus.rateLimiter.core.RateLimitResult;

import java.util.List;

public class RedisTokenBucketAlgorithm implements RedisRateLimitAlgorithm {
    private final long maxTokens;
    private final int refillRate;

    public RedisTokenBucketAlgorithm(long maxTokens, int refillRate) {
        this.maxTokens = maxTokens;
        this.refillRate = refillRate;
    }

    @Override
    public RateLimitResult tryConsume(String clientKey) {
        throw new UnsupportedOperationException("Use RedisStateStore to execute this algorithm");
    }

    @Override
    public String getScriptPath() {
        return "token_bucket.lua";
    }

    @Override
    public List<String> getArgv(String clientKey) {
        return List.of(
                String.valueOf(maxTokens),
                String.valueOf(refillRate)
        );
    }
}