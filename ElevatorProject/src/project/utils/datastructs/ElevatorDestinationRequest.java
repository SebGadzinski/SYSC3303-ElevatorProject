package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorDestinationRequest extends Request {

    private int requestedDestinationFloor;
    private ElevatorDirection direction;

    public ElevatorDestinationRequest(Source source, int requestedDestinationFloor, ElevatorDirection direction) {
        super(source);
        this.requestedDestinationFloor = requestedDestinationFloor;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Source: " + getSource() + "Set Destination to floor: " + this.requestedDestinationFloor + "\n";
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
