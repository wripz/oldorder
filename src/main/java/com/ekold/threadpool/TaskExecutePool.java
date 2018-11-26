package com.ekold.threadpool;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author:yangqiao
 * @description:
 * @Date:2018/3/10
 */
@Configuration
@ComponentScan
@EnableAsync
@EnableAutoConfiguration
public class TaskExecutePool {

    @Bean
    public Executor myTaskAsyncPool() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(30);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(60);
        //队列最大长度
        threadPoolTaskExecutor.setQueueCapacity(1000);
        //线程池维护线程所允许的时间
        threadPoolTaskExecutor.setKeepAliveSeconds(300);
        //自动回收线程
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setThreadNamePrefix("myExecutor-");
        // rejection-policy：当pool已经达到max size的时候，如何处理新任务
        // CALLER_RUNS：不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}