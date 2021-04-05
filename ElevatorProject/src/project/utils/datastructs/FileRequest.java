package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;;

/**
 * Thread Safe request for requests from input file. No setters because a
 * request should not be changed from its original purpose
 *
 * @author Sebastian Gadzinski
 */
public class FileRequest extends Request {

	private String time;
	private ElevatorDirection direction;
	private int destinationFloor, originFloor;
    private int fault;

	public FileRequest(String time, int originFloor, ElevatorDirection direction, int destinationFloor,
			SubsystemSource source) {
		super(source, 0);
		this.time = time;
		this.originFloor = originFloor;
		this.direction = direction;
		this.destinationFloor = destinationFloor;
		this.fault = -1;
	}
	
	public FileRequest(String time, int originFloor, ElevatorDirection direction, int destinationFloor,
			SubsystemSource source, int fault) {
		super(source, fault);
		this.time = time;
		this.originFloor = originFloor;
		this.direction = direction;
		this.destinationFloor = destinationFloor;
		this.fault = fault;
	}

	public String getTime() {
		return time;
	}

	public int getOriginFloor() {
		return originFloor;
	}

	public ElevatorDirection getDirection() {
		return direction;
	}

	public int getDestinationFloor() {
		return destinationFloor;
	}

	public int getFault() {
		return fault;
	}

	public void setFault(int fault) {
		this.fault = fault;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "FileRequest: " + "\n" + "Creation Time:  " + this.getTime() + "\n" + "Direction: " + this.getDirection() + "\n"
				+ "Pick Up: " + this.getOriginFloor() + "\n" + "Destination Floor: " + this.getDestinationFloor()
				+ "\nFault:  " + this.getFault();
	}

}
