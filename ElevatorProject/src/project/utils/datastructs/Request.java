package project.utils.datastructs;

public class Request {
	
    private Source source;
    
    public Request(Source source) {
		super();
		this.source = source;
	}

	public Source getSource() {
        return source;
    }
	
	public synchronized void setSource(Source source) {
		this.source = source;
    }
    
    public enum Source {
    	ELEVATOR_SUBSYSTEM, FLOOR_SUBSYSTEM, SCHEDULER
    }
	
}
