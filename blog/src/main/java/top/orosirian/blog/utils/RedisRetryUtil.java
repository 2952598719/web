package top.orosirian.blog.utils;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisRetryUtil {
    public static <T> T retryWithBackoff(Callable<T> operation, int maxRetries, long initialDelayMs) throws Exception {
        Exception lastException = null;
        long currentDelay = initialDelayMs;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                return operation.call();
            } catch (Exception e) {
                lastException = e;
                log.warn("Redis操作失败，第{}次重试（等待{}ms）", attempt, currentDelay, e);
                
                if (attempt < maxRetries) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(currentDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw ie;
                    }
                    currentDelay *= 2; // 指数退避
                }
            }
        }
        log.error("Redis操作最终失败，已达最大重试次数{}", maxRetries);
        throw lastException;
    }
}