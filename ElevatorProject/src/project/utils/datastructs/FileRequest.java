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
    private int destinatinoFloor, orginFloor;
    
	public FileRequest(String time, int orginFloor, ElevatorDirection direction, int destinatinoFloor, Source source) {
		super(source);
		this.time = time;
		this.orginFloor = orginFloor;
		this.direction = direction;
		this.destinatinoFloor = destinatinoFloor;
	}

	public String getTime() {
		return time;
	}

	public int getOrginFloor() {
		return orginFloor;
	}

	public ElevatorDirection getDirection() {
		return direction;
	}

	public int getDestinatinoFloor() {
		return destinatinoFloor;
	}

}
