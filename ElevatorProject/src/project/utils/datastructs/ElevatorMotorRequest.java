package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorMotorRequest extends Request {

	private ElevatorDirection requestedDirection;

	public ElevatorMotorRequest(SubsystemSource source, ElevatorDirection requestedDirection) {
		super(source);
		this.requestedDirection = requestedDirection;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "ElevatorMotorRequest: " + "\n"
		+ "Motor to Move:  " + this.requestedDirection + "\n";
	}

	public ElevatorDirection getRequestedDirection() {
		return requestedDirection;
	}

	public void setRequestedDirection(ElevatorDirection requestedDirection) {
		this.requestedDirection = requestedDirection;
	}

	
	
}
