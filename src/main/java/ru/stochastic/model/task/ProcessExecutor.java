package ru.stochastic.model.task;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import ru.stochastic.model.logic.QueueLogic;

import static java.util.Objects.nonNull;

@Slf4j
@RequiredArgsConstructor
public class ProcessExecutor extends Thread {

    @Value("${application.execution.count-client-processed}")
    private Long COUNT;
    @Value("${model.parameter.mu}")
    private double mu;
    private final QueueLogic logic;

    private static int executionCounter = 0;

    @Override
    public void run() {
        if (nonNull(COUNT) && executionCounter++ == COUNT) {
            System.exit(0);
        }
        if (logic.touch()) {
            var interval = (long) this.poissonRandomInterarrivalDelay(mu);
            try {
                this.sleep(interval);
                logic.process();
            } catch (InterruptedException e) {
                log.error("Lol crash");
            }
        }
    }

    public double poissonRandomInterarrivalDelay(double lambda) {
        return Math.log(1.0 - Math.random()) / -lambda * 1000;
    }
}
