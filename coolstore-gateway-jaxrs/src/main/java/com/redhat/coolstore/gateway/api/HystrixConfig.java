package com.redhat.coolstore.gateway.api;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import com.netflix.hystrix.Hystrix;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.strategy.HystrixPlugins;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategy;
import com.netflix.hystrix.strategy.properties.HystrixProperty;

@ApplicationScoped
public class HystrixConfig {

    // The default factory is used
    @Resource(lookup = "java:comp/DefaultManagedThreadFactory")
    ManagedThreadFactory threadFactory;

    // Initialize eagerly
    void init(@Observes @Initialized(ApplicationScoped.class) Object event) {
    }

    @PostConstruct
    public void onStartup() {
        HystrixPlugins.getInstance().registerConcurrencyStrategy(new HystrixConcurrencyStrategy() {
            @Override
            public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixProperty<Integer> corePoolSize,
                    HystrixProperty<Integer> maximumPoolSize, HystrixProperty<Integer> keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
                return new ThreadPoolExecutor(corePoolSize.get(), maximumPoolSize.get(), keepAliveTime.get(), unit, workQueue, threadFactory);
            }
        });
    }

    @PreDestroy
    public void onShutdown() {
        Hystrix.reset(1, TimeUnit.SECONDS);
    }

}