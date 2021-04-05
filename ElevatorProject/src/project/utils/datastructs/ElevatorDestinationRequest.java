package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorDestinationRequest extends Request {

    private int requestedDestinationFloor;
    private final ElevatorDirection direction;

    public ElevatorDestinationRequest(SubsystemSource source, int requestedDestinationFloor, ElevatorDirection direction) {
        super(source, 0);
        this.requestedDestinationFloor = requestedDestinationFloor;
        this.direction = direction;
    }
    
    public ElevatorDestinationRequest(SubsystemSource source, int requestedDestinationFloor, ElevatorDirection direction, int fault) {
		super(source, fault);
        this.requestedDestinationFloor = requestedDestinationFloor;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "ElevatorDestinationRequest:\nDestination: " + requestedDestinationFloor;
    }

    public int getRequestedDestinationFloor() {
        return requestedDestinationFloor;
    }

    public synchronized void setRequestedDestinationFloor(int requestedDestinationFloor) {
        this.requestedDestinationFloor = requestedDestinationFloor;
    }

    public ElevatorDirection getDirection() {
        return direction;
    }

}
