import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;

@RequiredArgsConstructor
public class LogScheduledTask extends Thread {

    private final Logic logic;
    private FileWriter out;
    private long second = 0;
    private final String[] HEADERS = {"second", "success", "rejected", "current_size"};

    @Override
    @SneakyThrows
    public void run() {
        out = new FileWriter("queue_metrics.csv", true);
        try (CSVPrinter printer = second == 0 ?
                new CSVPrinter(out, CSVFormat.DEFAULT.withHeader(HEADERS)) :
                new CSVPrinter(out, CSVFormat.DEFAULT)) {
            printer.printRecord(
                    ++second,
                    logic.getSuccessPushedClientCounter(),
                    logic.getRejectedClientCounter(),
                    logic.getInQueueCounter().get()
            );
        }
    }
}
