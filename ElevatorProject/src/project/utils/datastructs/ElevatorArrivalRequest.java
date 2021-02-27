package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorArrivalRequest extends Request {

	private int floorArrivedAt;
	private ElevatorDirection currentDirection;
	private int destinationFloor;


	public ElevatorArrivalRequest(Source source, int floorArrivedAt, ElevatorDirection currentDirection, int destinationFloor) {
		super(source);
		this.floorArrivedAt = floorArrivedAt;
		this.currentDirection = currentDirection;
		this.destinationFloor = destinationFloor;
	}

	@Override
	public String toString() {
		return "ElevatorArrivalRequest: " + "\n"
		+ "Source: " + this.getSource() + "\n"
		+ "Floor Arrived At: " + this.floorArrivedAt + "\n"
		+ "Current Direction: " + this.currentDirection + "\n"
		+ "Destination Floor: " + this.destinationFloor + "\n";
	}

	public int getFloorArrivedAt() {
		return floorArrivedAt;
	}

	public void setFloorArrivedAt(int floorArrivedAt) {
		this.floorArrivedAt = floorArrivedAt;
	}

	public ElevatorDirection getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(ElevatorDirection currentDirection) {
		this.currentDirection = currentDirection;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}
	
}
