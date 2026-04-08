package com.cerberus.rateLimiter;
import com.cerberus.rateLimiter.algorithm.redis.RedisFixedWindowAlgorithm;
import com.cerberus.rateLimiter.store.RedisStateStore;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.Throughput, Mode.AverageTime})
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Threads(50)
public class RedisBenchmark {

    private RedisStateStore store;
    private LettuceConnectionFactory connectionFactory;

    @Setup
    public void setup() throws IOException {
        connectionFactory = new LettuceConnectionFactory("localhost", 6379);
        connectionFactory.afterPropertiesSet();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();

        store = new RedisStateStore(redisTemplate, new RedisFixedWindowAlgorithm(Integer.MAX_VALUE, Duration.ofSeconds(60)));
    }

    @TearDown
    public void teardown() {
        connectionFactory.destroy();
    }

    @Benchmark
    public void benchmarkTryConsume() {
        store.tryConsume("bench-client");
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(RedisBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}