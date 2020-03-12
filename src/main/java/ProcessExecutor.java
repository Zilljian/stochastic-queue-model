import lombok.RequiredArgsConstructor;

import java.util.Random;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class ProcessExecutor extends Thread {

    private final static Logger log = Logger.getLogger("ClientEmitter");
    private final Logic logic;
    private final double mu;
    private final Random random = new Random(10);

    @Override
    public void run() {
        if (logic.touch()) {
            var interval = (long) this.poissonRandomInterarrivalDelay(mu);
            try {
                this.sleep(interval);
                logic.process();
            } catch (InterruptedException e) {
                log.warning("Lol crash");
            }
        }
    }

    public double poissonRandomInterarrivalDelay(double lambda) {
        lambda = -lambda;
        return Math.log(1.0 - random.nextDouble()) / lambda * 1000;
    }
}
