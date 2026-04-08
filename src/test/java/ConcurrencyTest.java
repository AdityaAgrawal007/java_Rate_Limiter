import com.cerberus.rateLimiter.algorithm.imMemoryFallback.FixedWindowAlgorithm;
import com.cerberus.rateLimiter.core.RateLimitResult;
import com.cerberus.rateLimiter.store.InMemoryStateStore;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConcurrencyTest {
    @Test
    void noViolationsUnder50ConcurrentThreads() throws InterruptedException {
        int THREADS = 50;
        int TOKEN_LIMIT = 10;

        InMemoryStateStore store = new InMemoryStateStore(new FixedWindowAlgorithm(TOKEN_LIMIT, Duration.ofSeconds(60)));
        CountDownLatch gate = new CountDownLatch(1);
        CountDownLatch waitAfterFinish = new CountDownLatch(THREADS);
        AtomicInteger noOfAcceptedReq = new AtomicInteger(0);
        ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; ++i) {
            pool.submit(() -> {
                try {
                    gate.await();
                    RateLimitResult result = store.tryConsume("client01");
                    if (result.accepted()) noOfAcceptedReq.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    waitAfterFinish.countDown();
                }
            });
        }
        gate.countDown();
        waitAfterFinish.await();
        pool.shutdown();

        assertEquals(TOKEN_LIMIT, noOfAcceptedReq.get());
    }

    public static void main(String args[]) throws InterruptedException {
        ConcurrencyTest obj = new ConcurrencyTest();
        for (int i = 1; i < 1000; ++i) {
            obj.noViolationsUnder50ConcurrentThreads();
        }
    }

}
