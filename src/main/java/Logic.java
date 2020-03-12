import lombok.Getter;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Getter
public class Logic {

    private final static Logger log = Logger.getLogger("Process");
    private final Queue<Client> queue;
    private AtomicLong inQueueCounter = new AtomicLong(0);
    private long successPushedClientCounter = 0;
    private long rejectedClientCounter = 0;

    public Logic(int maxQueueDepth) {
        this.queue = new ArrayBlockingQueue<>(maxQueueDepth);
    }

    public void process() {
        var client = queue.poll();
        if (isNull(client)) {
            return;
        }
        inQueueCounter.getAndDecrement();
        client.latchProcess();
        log.info(format("Time: %s Client has left the queue, id = %s",
                LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
                client.getId()));
    }

    public void push(Client client) {
        if (queue.offer(client)) {
            successPushedClientCounter++;
            inQueueCounter.getAndIncrement();
            log.info(format("Time: %s New client has entered the queue, id = %s",
                    LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
                    client.getId()));
        } else {
            rejectedClientCounter++;
            log.info(format("Time: %s New client has been rejected from queue, id = %s",
                    LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
                    client.getId()));
        }
    }

    public boolean touch() {
        var client = queue.peek();
        if (isNull(client)) {
            return false;
        }
        client.latchQueue();
        log.info(format("Time: %s Processing client, id = %s",
                LocalTime.now().truncatedTo(ChronoUnit.SECONDS),
                client.getId()));
        return true;
    }
}
