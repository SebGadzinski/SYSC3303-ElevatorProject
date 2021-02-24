package project.state_machines;


public class ElevatorState{
	
	private ElevatorStateStatus state;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
	private int currentFloor;
	
	public ElevatorState(ElevatorStateStatus state, ElevatorDoorStatus doorState,
			ElevatorDirection directionState, int currentFloor) {
		super();
		this.state = state;
		this.doorState = doorState;
		this.directionState = directionState;
		this.currentFloor = currentFloor;
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

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
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


