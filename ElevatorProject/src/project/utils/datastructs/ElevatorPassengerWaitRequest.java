package project.utils.datastructs;


public class ElevatorPassengerWaitRequest extends Request{

	private int waitTime;
	private WaitState state ;

	public ElevatorPassengerWaitRequest(Source source, int waitTime, WaitState state) {
		super(source);
		this.waitTime = waitTime;
		this.state = state;
	}

	public enum WaitState{
		WAITING, FINISHED;
	}

	public int getWaitTime() {
		return waitTime;
	}

	public void setWaitTime(int waitTime) {
		this.waitTime = waitTime;
	}

	public WaitState getState() {
		return state;
	}

	public void setState(WaitState state) {
		this.state = state;
	}
	
}
