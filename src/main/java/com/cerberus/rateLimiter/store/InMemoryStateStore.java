// data stored per client key -  window start timestamp, count of requested tokens till now
package com.cerberus.rateLimiter.store;

import com.cerberus.rateLimiter.algorithm.RateLimitAlgorithm;
import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.core.StateStore;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStateStore implements StateStore {
    private final RateLimitAlgorithm algorithm;

    public InMemoryStateStore(RateLimitAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    public RateLimitResult tryConsume(String clientKey) {
        return algorithm.tryConsume(clientKey);
    }
}