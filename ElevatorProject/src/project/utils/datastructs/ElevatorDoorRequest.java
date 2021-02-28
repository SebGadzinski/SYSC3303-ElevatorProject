package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class ElevatorDoorRequest extends Request {

	private ElevatorDoorStatus requestedDoorStatus;

	public ElevatorDoorRequest(Source source, ElevatorDoorStatus requestedDoorStatus) {
		super(source);
		this.requestedDoorStatus = requestedDoorStatus;
	}

	@Override
	public String toString() {
		return "ElevatorDoorRequest: " + "\n"
		+ "Source: " + this.getSource() + "\n"
		+ "Request for Doors to be: " + this.requestedDoorStatus + "\n";
	}

	public ElevatorDoorStatus getRequestedDoorStatus() {
		return requestedDoorStatus;
	}

	public void setRequestedDoorStatus(ElevatorDoorStatus requestedDoorStatus) {
		this.requestedDoorStatus = requestedDoorStatus;
	}
	
}
