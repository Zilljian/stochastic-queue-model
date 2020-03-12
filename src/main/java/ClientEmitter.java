import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ClientEmitter extends Thread {

    private final static Logger log = Logger.getLogger("ClientEmitter");
    private final Logic logic;
    private final double lambda;
    private final Random random = new Random(10);

    @Override
    public void run() {
        var interval = (long) this.poissonRandomInterarrivalDelay(lambda);
        try {
            this.sleep(interval);
            logic.push(new Client());
        } catch (InterruptedException e) {
            log.warning("Lol crash");
        }
    }

    public double poissonRandomInterarrivalDelay(double lambda) {
        lambda = - lambda;
        return Math.log(1.0 - random.nextDouble()) / lambda * 1000;
    }
}
