package com.cerberus.rateLimiter;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rate-limiter")
public class RateLimiterProperties {
    private String algorithm;
    private long limit;
    private long timeWindow;
    private long maxTokens;
    private long refillRate;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public long getLimit() {
        return limit;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(long timeWindow) {
        this.timeWindow = timeWindow;
    }

    public void setWindow(long window) {
        this.timeWindow = window;
    }

    public long getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(long maxTokens) {
        this.maxTokens = maxTokens;
    }

    public long getRefillRate() {
        return refillRate;
    }

    public void setRefillRate(long refillRate) {
        this.refillRate = refillRate;
    }
}