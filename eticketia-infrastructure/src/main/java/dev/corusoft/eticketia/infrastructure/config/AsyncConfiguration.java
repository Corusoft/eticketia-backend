package dev.corusoft.eticketia.infrastructure.config;

import java.util.concurrent.Executor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Configuration for executing asynchronous tasks.
 */
@Configuration
@EnableAsync
public class AsyncConfiguration {

  @Bean(name = "taskExecutor")
  public Executor taskExecutor() {
    SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
    executor.setVirtualThreads(true);
    executor.setThreadNamePrefix("TaskExecutor-");

    return executor;
  }
}
