package com.cerberus.rateLimiter.algorithm;

// client info we need to store -
// 1. count of tokens present right now
// 2. timestamp of last made request
public record BucketState(long lastRefillTimestamp, long tokens) {
}
