import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Application {

    public static void main(String[] args) {
        var logic = new Logic(10);
        var emitter = new ClientEmitter(logic, 10);
        var executor = new ProcessExecutor(logic, 5);
        var queueLogger = new LogScheduledTask(logic);
        var scheduler = Executors.newScheduledThreadPool(3);
        scheduler.scheduleWithFixedDelay(emitter, 1, 10, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(executor, 1, 10, TimeUnit.MILLISECONDS);
        scheduler.scheduleWithFixedDelay(queueLogger, 1, 1, TimeUnit.SECONDS);
    }
}
