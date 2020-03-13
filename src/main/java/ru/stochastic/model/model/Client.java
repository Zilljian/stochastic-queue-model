package ru.stochastic.model.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileWriter;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Client {

    @Value("${metrics.location.client}")
    private String FILE_OUTPUT;

    private static long idGenerator;
    private final long id;
    private final String[] HEADERS = {"id", "queue_time", "process_time"};
    private final LocalDateTime emitted;

    private LocalDateTime queueEnd;
    private LocalDateTime processEnd;

    @SneakyThrows
    @PostConstruct
    private void clearOutput() {
        var out = new FileWriter(FILE_OUTPUT, false);
        try (var ignored = new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS))) {
        }
    }

    public Client() {
        this.id = idGenerator++;
        this.emitted = LocalDateTime.now();
    }

    public void latchQueue() {
        this.queueEnd = LocalDateTime.now();
    }

    public void latchProcess() {
        this.processEnd = LocalDateTime.now();
    }

    public Duration getTimeInQueue() {
        return Duration.between(this.emitted, this.queueEnd);
    }

    public Duration getTimeInProcess() {
        return Duration.between(this.emitted, this.processEnd);
    }

    @SneakyThrows
    public void writeStatistics() {
        var out = new FileWriter(FILE_OUTPUT, true);
        try (var printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(
                    id,
                    (double) getTimeInQueue().getNano() / 1000000.0,
                    (double) getTimeInProcess().getNano() / 1000000.0
            );
        }
    }
}
