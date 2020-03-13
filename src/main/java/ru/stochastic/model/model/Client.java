package ru.stochastic.model.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@Component
@RequiredArgsConstructor
public class Client {

    private static final String METRICS_FILE = "src/main/resources/metrics/client_metrics.csv";
    private static long idGenerator;
    private final long id;
    private final LocalDateTime emitted;
    private LocalDateTime queueEnd;
    private LocalDateTime processEnd;

    static {
        FileWriter out = null;
        try {
            out = new FileWriter(METRICS_FILE, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (var ignored = new CSVPrinter(
                out, CSVFormat.DEFAULT
                .withIgnoreEmptyLines()
                .withHeader("id", "queue_time", "process_time"))) {
        } catch (IOException e) {
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
        var out = new FileWriter(METRICS_FILE, true);
        try (var printer = new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(
                    id,
                    (double) getTimeInQueue().getNano() / 1000000.0,
                    (double) getTimeInProcess().getNano() / 1000000.0
            );
        }
    }
}
