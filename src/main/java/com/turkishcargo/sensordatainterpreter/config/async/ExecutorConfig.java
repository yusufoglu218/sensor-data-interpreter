package com.turkishcargo.sensordatainterpreter.config.async;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorConfig {

    @Value("${sensor.executor.core.pool-size:10}")
    private int corePoolSize;

    @Value("${sensor.executor.max.pool-size:50}")
    private int maxPoolSize;

    @Value("${sensor.executor.queue-capacity:1000}")
    private int queueCapacity;

    @Bean(name = "sensorExecutor")
    public ThreadPoolExecutor sensorExecutor() {
        BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(queueCapacity);

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                60L,
                TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
}
