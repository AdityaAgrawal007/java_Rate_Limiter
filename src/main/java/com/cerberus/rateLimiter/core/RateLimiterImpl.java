package com.cerberus.rateLimiter.core;

import com.cerberus.rateLimiter.extractor.IpKeyExtractor;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import jakarta.servlet.http.HttpServletRequest;


import java.time.Duration;

public class RateLimiterImpl implements RateLimiter{
    private StateStore store;

    public RateLimiterImpl(StateStore store) {
        this.store = store;
    }

    public RateLimitResult tryAquire(String clientId) {
        return store.tryConsume(clientId);
    }
}