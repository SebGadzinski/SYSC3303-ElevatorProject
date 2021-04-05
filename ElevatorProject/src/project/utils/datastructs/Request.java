package project.utils.datastructs;

import java.io.Serializable;

/**
 * Concrete specializations of this thread-safe class provide means of communication between subsystems.
 *
 * @author Paul Roode, Sebastian Gadzinski
 * @version Iteration 3
 */
public abstract class Request implements Serializable {

    private SubsystemSource source;
    private int fault = 0;

    public Request(SubsystemSource source, int fault) {
        this.source = source;
        this.fault = (fault != 0 ? fault : 0);
    }

    public SubsystemSource getSource() {
        return source;
    }

    public synchronized void setSource(SubsystemSource source) {
        this.source = source;
    }

	public int getFault() {
		return fault;
	}

	public void setFault(int fault) {
		this.fault = fault;
	}
    

}
