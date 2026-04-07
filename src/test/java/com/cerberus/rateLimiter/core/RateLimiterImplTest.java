//package com.cerberus.rateLimiter.core;
//
//import com.cerberus.rateLimiter.store.InMemoryStateStore;
//
//import java.time.Duration;
//
//public class RateLimiterImplTest {
//    public static void main(String args[]) {
//        StateStore store = new InMemoryStateStore();
//        RateLimiterImpl rateLimiter = new RateLimiterImpl(store);
//        for (int i = 1; i <= 10; ++i) {
//            RateLimitResult result = rateLimiter.tryAquire("client01");
//            System.out.println(i + ": " + result);
//        }
//
//        for (int i = 1; i <= 10; ++i) {
//            RateLimitResult result = rateLimiter.tryAquire("client02");
//            System.out.println(i + ": " + result);
//        }
//    }
//}
