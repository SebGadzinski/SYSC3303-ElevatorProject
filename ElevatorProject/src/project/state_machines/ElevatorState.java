package project.state_machines;


public class ElevatorState{
	
	private ElevatorStateStatus state;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
	
	public ElevatorState(ElevatorStateStatus state, ElevatorDoorStatus doorState,
			ElevatorDirection directionState) {
		super();
		this.state = state;
		this.doorState = doorState;
		this.directionState = directionState;
	}

	public ElevatorStateStatus getState() {
		return state;
	}

	public void setState(ElevatorStateStatus state) {
		this.state = state;
	}

	public ElevatorDoorStatus getDoorState() {
		return doorState;
	}

	public void setDoorState(ElevatorDoorStatus doorState) {
		this.doorState = doorState;
	}

	public ElevatorDirection getDirectionState() {
		return directionState;
	}

	public void setDirectionState(ElevatorDirection directionState) {
		this.directionState = directionState;
	}	
	
	public enum ElevatorStateStatus{
		IDLE, ELEVATOR_ARRIVAL, ELEVATOR_MOVING, ELEVATOR_OPENING_DOORS, ELEVATOR_CLOSING_DOORS, PASSENGER_HANDLING
	}

	public enum ElevatorDoorStatus{
		OPENED, CLOSED
	}

	public enum ElevatorDirection{
		UP, DOWN, IDLE
	}

	
}


