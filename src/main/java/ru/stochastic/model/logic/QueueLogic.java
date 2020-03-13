package ru.stochastic.model.logic;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.stochastic.model.model.Client;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.String.format;
import static java.util.Objects.isNull;

@Slf4j
@Getter
public class QueueLogic {

    private final Queue<Client> queue;
    private final List<Client> processedClients = new ArrayList<>();
    private final AtomicLong inQueueCounter = new AtomicLong(0);
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    private long successPushedClientCounter = 0;
    private long rejectedClientCounter = 0;

    public QueueLogic(int maxQueueDepth) {
        this.queue = new ArrayBlockingQueue<>(maxQueueDepth);
    }

    public void process() {
        var client = queue.poll();
        if (isNull(client)) {
            return;
        }
        inQueueCounter.getAndDecrement();
        client.latchProcess();
        executorService.submit(client::writeStatistics);
        log.info(format("Time: %s model.Client has left the queue, id = %s",
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
