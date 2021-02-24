package project.utils.datastructs;

import project.state_machines.ElevatorState.ElevatorDirection;

public class ElevatorArrivalRequest extends Request{

	private int floorArrivedAt;
	private ElevatorDirection currentDirection;


	public ElevatorArrivalRequest(Source source, int floorArrivedAt, ElevatorDirection currentDirection) {
		super(source);
		this.floorArrivedAt = floorArrivedAt;
		this.currentDirection = currentDirection;
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Source: " + this.getSource()
		+ "Floor Arrived At: " + this.floorArrivedAt + "\n"
		+ "Current Direction: " + this.floorArrivedAt + "\n";
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

	
	
}
