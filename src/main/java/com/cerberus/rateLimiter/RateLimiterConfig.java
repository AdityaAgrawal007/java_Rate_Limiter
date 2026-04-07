package com.cerberus.rateLimiter;

import com.cerberus.rateLimiter.algorithm.redis.RedisFixedWindowAlgorithm;
import com.cerberus.rateLimiter.algorithm.redis.RedisRateLimitAlgorithm;
import com.cerberus.rateLimiter.algorithm.redis.RedisTokenBucketAlgorithm;
import com.cerberus.rateLimiter.core.RateLimiter;
import com.cerberus.rateLimiter.core.RateLimiterImpl;
import com.cerberus.rateLimiter.extractor.IpKeyExtractor;
import com.cerberus.rateLimiter.interceptor.RateLimitInterceptor;
import com.cerberus.rateLimiter.store.RedisStateStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.Duration;

@Configuration
public class RateLimiterConfig implements WebMvcConfigurer {
    @Autowired
    private RateLimiterProperties rateLimiterProperties;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Bean
    public RedisRateLimitAlgorithm redisFixedWindowAlgorithm() {
        return new RedisFixedWindowAlgorithm(rateLimiterProperties.getLimit(), Duration.ofSeconds(rateLimiterProperties.getTimeWindow()));
    }

    @Primary // this annotations makes this defalut bean that will be injected
    @Bean
    public RedisRateLimitAlgorithm redisTokenBucketAlgorithm() {
        return new RedisTokenBucketAlgorithm(rateLimiterProperties.getMaxTokens(), (int) rateLimiterProperties.getRefillRate());
    }

    @Bean
    public RedisStateStore redisStateStore() throws IOException {
        // if more algorithms come we need a different mechanism to do the following if else blocks aren't the best
        if ("FIXED_WINDOW".equals(rateLimiterProperties.getAlgorithm())) {
            return new RedisStateStore(redisTemplate, redisFixedWindowAlgorithm());
        } else {
            return new RedisStateStore(redisTemplate, redisTokenBucketAlgorithm());
        }
    }

    @Bean
    public RateLimiter rateLimiter() throws IOException {
        return new RateLimiterImpl(redisStateStore());
    }

    @Bean
    public RateLimitInterceptor rateLimitInterceptor() throws IOException {
        return new RateLimitInterceptor(rateLimiter(), new IpKeyExtractor());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        try {
            registry.addInterceptor(rateLimitInterceptor());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}