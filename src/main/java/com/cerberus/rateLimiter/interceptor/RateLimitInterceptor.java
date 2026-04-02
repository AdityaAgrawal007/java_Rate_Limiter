package com.cerberus.rateLimiter.interceptor;

import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.core.RateLimiter;
import com.cerberus.rateLimiter.extractor.KeyExtractor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;


public class RateLimitInterceptor implements HandlerInterceptor {
    private final RateLimiter rateLimiter;
    private final KeyExtractor keyExtractor;

    public RateLimitInterceptor(RateLimiter rateLimiter, KeyExtractor keyExtractor) {
        this.rateLimiter = rateLimiter;
        this.keyExtractor = keyExtractor;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientId = keyExtractor.extractRemoteAddr(request);
        RateLimitResult result = rateLimiter.tryAquire(clientId);
        if(!result.accepted()){
            response.setStatus(429);
        }
        response.setHeader("X-RateLimit-Remaining", String.valueOf(result.remainingTokens()));
        response.setHeader("X-RateLimit-Reset", String.valueOf(result.resetTimestamp()));
        return result.accepted();
    }
}