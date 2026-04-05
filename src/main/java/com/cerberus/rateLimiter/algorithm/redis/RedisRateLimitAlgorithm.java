package com.cerberus.rateLimiter.algorithm.redis;

import com.cerberus.rateLimiter.algorithm.RateLimitAlgorithm;

import java.util.List;

public interface RedisRateLimitAlgorithm extends RateLimitAlgorithm {
    String getScriptPath();
    List<String> getArgv(String clientKey);
}