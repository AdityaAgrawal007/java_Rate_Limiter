package com.cerberus.rateLimiter.algorithm;
//  this interface is meant to be implemented by the algorithms used unlike StateStore which is meant to be implemented by the handler of
// these algorithms
import com.cerberus.rateLimiter.core.RateLimitResult;
import java.time.Duration;

public interface RateLimitAlgorithm {
    RateLimitResult tryConsume(String clientKey);
}
