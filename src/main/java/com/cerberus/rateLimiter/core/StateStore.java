// we are making this as an interface so that we can later swap in memory implementation with redis
// this is ment to be implemented by the handlers of algorithms unlike RateLimitAlgorithms that are meant to be implemented
// by the algorithms themselves
package com.cerberus.rateLimiter.core;
import java.time.Duration;
import java.util.Dictionary;

//  all variables in an interface are by default public final static and hence must initialize
public interface StateStore {
    // duration represents length of time like for 5req/min, duration is 1 min
    RateLimitResult tryConsume(String clientKey);
}
