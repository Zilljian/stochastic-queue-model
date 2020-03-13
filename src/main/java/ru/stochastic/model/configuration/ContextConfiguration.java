package ru.stochastic.model.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.stochastic.model.logic.QueueLogic;
import ru.stochastic.model.task.ClientEmitter;
import ru.stochastic.model.task.LogScheduledTask;
import ru.stochastic.model.task.ProcessExecutor;

@Configuration
public class ContextConfiguration {

    @Bean
    public ClientEmitter clientEmitter(QueueLogic queueLogic) {
        return new ClientEmitter(queueLogic);
    }

    @Bean
    public ProcessExecutor processExecutor(QueueLogic queueLogic) {
        return new ProcessExecutor(queueLogic);
    }

    @Bean
    public LogScheduledTask logScheduledTask(QueueLogic queueLogic) {
        return new LogScheduledTask(queueLogic);
    }

    @Bean
    public QueueLogic queueLogic(@Value("${model.parameter.queue-depth}") int queueDepth) {
        return new QueueLogic(queueDepth);
    }
}
