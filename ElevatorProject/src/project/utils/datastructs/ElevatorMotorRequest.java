package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;

public class ElevatorMotorRequest extends Request {

    private ElevatorDirection requestedDirection;

    public ElevatorMotorRequest(SubsystemSource source, ElevatorDirection requestedDirection, int fault) {
		super(source, fault);
        this.requestedDirection = requestedDirection;
    }
    
    public ElevatorMotorRequest(SubsystemSource source, ElevatorDirection requestedDirection) {
		super(source, 0);
        this.requestedDirection = requestedDirection;
    }

    @Override
    public String toString() {
        return "ElevatorMotorRequest:\nMotor to move: " + requestedDirection + "\n";
    }

    public ElevatorDirection getRequestedDirection() {
        return requestedDirection;
    }

    public void setRequestedDirection(ElevatorDirection requestedDirection) {
        this.requestedDirection = requestedDirection;
    }

}
