//package com.cerberus.rateLimiter.store;
//
//import com.cerberus.rateLimiter.core.RateLimitResult;
//
//import java.time.Duration;
//
//public class InMemoryStateStoreTest {
//    public static void main(String agrs[]) {
//        InMemoryStateStore obj = new InMemoryStateStore();
//        for (int i = 1; i <= 10; ++i) {
//            RateLimitResult result = obj.tryConsume("client01", 5, Duration.ofMinutes(1));
//            System.out.println("Request " + i + ": " + result);
//        }
//    }
//}
//
//// Request 0: RateLimitResult[accepted=true, resetTimestamp=1775063439, remainingTokens=4]
//// here the value of timestamp is the no. of seconds since Unix epoch Jan 1, 1970