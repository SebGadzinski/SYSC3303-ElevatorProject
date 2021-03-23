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
    private int containsFault = -1;

    public Request(SubsystemSource source) {
        this.source = source;
    }

    public SubsystemSource getSource() {
        return source;
    }

    public synchronized void setSource(SubsystemSource source) {
        this.source = source;
    }

	public int getContainsFault() {
		return containsFault;
	}

	public void setContainsFault(int containsFault) {
		this.containsFault = containsFault;
	}
    
    

}
