package project.utils.datastructs;

import project.state_machines.ElevatorState.ElevatorDirection;

/**
 * Thread Safe request for requests from input file. No setters because a request should not be changed from its original purpose
 *
 * @author Sebastian Gadzinski
 */
public class FileRequest extends Request{

    private String time;
    private ElevatorDirection direction;
    private int destinationFloor, originFloor;
    
	public FileRequest(String time, int orginFloor, ElevatorDirection direction, int destinatinoFloor, Source source) {
		super(source);
		this.time = time;
		this.originFloor = orginFloor;
		this.direction = direction;
		this.destinationFloor = destinatinoFloor;
	}

	public String getTime() {
		return time;
	}

	public int getOrginFloor() {
		return originFloor;
	}

	public ElevatorDirection getDirection() {
		return direction;
	}

	public int getDestinatinoFloor() {
		return destinationFloor;
	}

}
