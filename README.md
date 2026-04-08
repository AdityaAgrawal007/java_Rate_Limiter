Cerberus Rate Limiter is a rate limiting library for Spring Boot applications. Add it as a Maven dependency and your endpoints are protected, no boilerplate configuration classes required. It supports Fixed Window and Token Bucket algorithms, switchable via a single property, backed by Redis for distributed deployments with atomic Lua-script enforcement to prevent race conditions across instances. An in-memory fallback is available for local development.
## Features

- **Two rate limiting algorithms** - Fixed Window and Token Bucket, switchable via `application.yml` with no code changes
- **Redis-backed distributed limiting** - atomic Lua script execution ensures no race conditions across multiple instances
- **In-memory fallback** - for local development and testing, no Redis required
- **Spring Boot Auto-configuration** - zero boilerplate, just add the Maven dependency and configure properties
- **Concurrency-safe** - `ConcurrentHashMap.compute` ensures atomicity at the algorithm level, not just the database level
- **Per-client isolation** - each client key maintains independent counters
- **Configurable via YAML** - limit, window size, algorithm, token bucket parameters all externalized

## Requirements

- Java 21+
- Spring Boot 4.x
- Redis (for distributed mode)

## Installation

Add to your `pom.xml`:

```xml
<dependency>
    <groupId>com.cerberus</groupId>
    <artifactId>rateLimiter</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## Configuration

```yaml
rate-limiter:
  algorithm: FIXED_WINDOW   # or TOKEN_BUCKET
  limit: 100
  window: 60                # seconds
  max-tokens: 100           # TOKEN_BUCKET only
  refill-rate: 10           # TOKEN_BUCKET only, tokens per second
```

## How It Works

Incoming requests pass through a Spring `HandlerInterceptor` that extracts the client key (IP address by default) and calls `tryConsume` on the state store. For Redis, the entire read-check-write cycle executes as a single atomic Lua script on the Redis server, preventing race conditions across distributed instances. For in-memory, `ConcurrentHashMap.compute` guarantees atomicity at the JVM level.

## Performance

Benchmarked with JMH under 50 concurrent threads:

| | In-Memory | Redis |
|---|---|---|
| Throughput | 13.9M ops/sec | 67K ops/sec |
| Avg Latency | 3.6 μs/op | 0.75 ms/op |

## Testing

- **Correctness** - exact limit enforcement, window reset behavior, per-client isolation verified
- **Concurrency** - zero violations across 1,000 concurrent test runs with 50 threads (CountDownLatch)
- **Performance** - JMH benchmarks for both in-memory and Redis implementations
