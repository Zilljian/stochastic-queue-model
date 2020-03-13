package ru.stochastic.model.task;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import ru.stochastic.model.logic.QueueLogic;

import javax.annotation.PostConstruct;
import java.io.FileWriter;

import static java.util.Objects.nonNull;

@RequiredArgsConstructor
public class LogScheduledTask extends Thread {

    @Value("${application.execution.time-seconds}")
    private Long COUNT;
    @Value("${metrics.location.queue}")
    private String FILE_OUTPUT;

    private static int executionCounter = 0;
    private final QueueLogic logic;
    private final String[] HEADERS = {"second", "success", "rejected", "current_size"};
    private long second = 0;

    @SneakyThrows
    @PostConstruct
    private void clearOutput() {
        try (var out = new FileWriter(FILE_OUTPUT, false)) {
            try (var ignored = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {
            }
        }
    }

    @Override
    @SneakyThrows
    public void run() {
        if (nonNull(COUNT) && executionCounter++ == COUNT) {
            System.exit(0);
        }
        try (var out = new FileWriter(FILE_OUTPUT, true)) {
            try (var printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
                printer.printRecord(
                        ++second,
                        logic.getSuccessPushedClientCounter(),
                        logic.getRejectedClientCounter(),
                        logic.getInQueueCounter().get()
                );
            }
        }
    }
}
