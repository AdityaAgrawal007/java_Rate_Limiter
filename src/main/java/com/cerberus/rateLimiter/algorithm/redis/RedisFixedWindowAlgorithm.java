package com.cerberus.rateLimiter.algorithm.redis;

import com.cerberus.rateLimiter.core.RateLimitResult;
import java.time.Duration;
import java.util.List;

public class RedisFixedWindowAlgorithm implements RedisRateLimitAlgorithm {
    private final long tokenLimit;
    private final Duration timeWindow;

    public RedisFixedWindowAlgorithm(long tokenLimit, Duration timeWindow) {
        this.tokenLimit = tokenLimit;
        this.timeWindow = timeWindow;
    }

    @Override
    public RateLimitResult tryConsume(String clientKey) {
        throw new UnsupportedOperationException("Use RedisStateStore to execute this algorithm");
    }

    @Override
    public String getScriptPath() {
        return "fixed_window.lua";
    }

    @Override
    public List<String> getArgv(String clientKey) {
        return List.of(
                String.valueOf(tokenLimit),
                String.valueOf(timeWindow.getSeconds())
        );
    }
}