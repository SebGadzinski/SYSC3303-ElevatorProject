package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorArrivalRequest extends Request {

    private int floorArrivedAt;
    private ElevatorDirection currentDirection;

    public ElevatorArrivalRequest(SubsystemSource source, int floorArrivedAt, ElevatorDirection currentDirection) {
        super(source, 0);
        this.floorArrivedAt = floorArrivedAt;
        this.currentDirection = currentDirection;
    }
    
    public ElevatorArrivalRequest(SubsystemSource source, int floorArrivedAt, ElevatorDirection currentDirection, int fault) {
		super(source, fault);
        this.floorArrivedAt = floorArrivedAt;
        this.currentDirection = currentDirection;
    }


    @Override
    public String toString() {
        return "ElevatorArrivalRequest:\nFloor arrived at: " + floorArrivedAt + "\nCurrent direction: " + currentDirection;
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
