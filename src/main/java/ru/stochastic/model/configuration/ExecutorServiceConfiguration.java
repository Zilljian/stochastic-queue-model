package ru.stochastic.model.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.stochastic.model.task.ClientEmitter;
import ru.stochastic.model.task.LogScheduledTask;
import ru.stochastic.model.task.ProcessExecutor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Configuration
public class ExecutorServiceConfiguration {

    @Bean
    public ExecutorService executorService(ClientEmitter clientEmitter,
                                           ProcessExecutor processExecutor,
                                           LogScheduledTask logScheduledTask) {
        var scheduler = Executors.newScheduledThreadPool(3);
        scheduler.scheduleWithFixedDelay(clientEmitter, 1, 10, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(processExecutor, 1, 10, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(logScheduledTask, 1, 1, TimeUnit.SECONDS);
        return Executors.newScheduledThreadPool(3);
    }
}
