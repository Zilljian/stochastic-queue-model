import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class Client {

    private static long idGenerator;
    private final long id;
    private final LocalDateTime emitted;
    private LocalDateTime queueEnd;
    private LocalDateTime processEnd;

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
}
