// data stored per client key -  window start timestamp, count of requested tokens till now
package com.cerberus.rateLimiter.store;

import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.core.StateStore;

import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStateStore implements StateStore {

    @Override
    public RateLimitResult tryConsume(String clientKey, long tokenLimit, Duration timeWindow) {
        RateLimitResult[] holder = new RateLimitResult[1];
        // the lambda allows threads working for two different users to work parallaly but two different threads trying to edit the same
        // user is prohibited by it
        map.compute(clientKey, (key, existingValue) -> {
            long currentTime = System.currentTimeMillis() / 1000;

            // compute method ensures that read, check, write is an "atomic operation" else say two threads read then then go to edit
            // both try to edit simoltaneosly then concurrent map would ensure that both get added but thats handling concurency at a lower level
            // at a higher level we need to ensure one thread doesn't sneak in between that window

            // client does not exists
            if (existingValue == null) {
//                System.out.println("Hello from new client creator");
                WindowState new_client = new WindowState(System.currentTimeMillis() / 1000, 1);
                long remainingTokens = tokenLimit - new_client.count();
                RateLimitResult result = new RateLimitResult(true, new_client.startTimestamp() + timeWindow.get(ChronoUnit.SECONDS), remainingTokens);
                holder[0] = result;
                return new_client;
            }

//            System.out.println("sanity check");
            // if client makes request after timeWindow
            if ((currentTime - existingValue.startTimestamp()) >= timeWindow.get(ChronoUnit.SECONDS)) {
//              long newStartTimestamp = map.get(clientKey).startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
                long newStartTimestamp = existingValue.startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
                WindowState element = new WindowState(newStartTimestamp, 1);
//                map.put(clientKey, element);
//                long resetTimeStamp = map.get(clientKey).startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
                long resetTimeStamp = existingValue.startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
                RateLimitResult result = new RateLimitResult(true, resetTimeStamp, tokenLimit - existingValue.count());
                holder[0] = result;
                return element;
            }

            // if client makes request within timeWindow
            else {
//                System.out.println("count: " + existingValue.count());
                // if client does not have enough tokens
                if ((existingValue.count() + 1) > tokenLimit) {
                    long resetTimeStamp = existingValue.startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
                    RateLimitResult result = new RateLimitResult(false, resetTimeStamp, tokenLimit - existingValue.count());
                    holder[0] = result;
                    return existingValue;
                }
            }

            // client makes request withing time window + has enough tokens
            WindowState element = new WindowState(existingValue.startTimestamp(), existingValue.count() + 1);
            long resetTimeStamp = existingValue.startTimestamp() + timeWindow.get(ChronoUnit.SECONDS);
            RateLimitResult result = new RateLimitResult(true, resetTimeStamp, tokenLimit - element.count());
            holder[0] = result;
            return element;
        });
        return holder[0];
    }

    ;

    // final - vars cannot be modified, methods cannot be overriden, classess cannot be extended, below map is reference to
    // object so it locks the reference not the object itself and thus it can be edited, map can only point to that object
    private final ConcurrentHashMap<String, WindowState> map = new ConcurrentHashMap<>();

}