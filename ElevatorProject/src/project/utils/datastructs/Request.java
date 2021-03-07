package project.utils.datastructs;

import java.io.Serializable;

/**
 * Concrete specializations of this thread-safe class provide means of communication between subsystems.
 *
 * @author Paul Roode, Sebastian Gadzinski
 * @version Iteration 3
 */
public abstract class Request implements Serializable {

    private Source source;

    /**
     * Subsystems from which a concrete Request specialization can be transmitted.
     */
    public enum Source {
        ELEVATOR_SUBSYSTEM, FLOOR_SUBSYSTEM, SCHEDULER
    }

    public Request(Source source) {
        this.source = source;
    }

    public Source getSource() {
        return source;
    }

    public synchronized void setSource(Source source) {
        this.source = source;
    }

}
