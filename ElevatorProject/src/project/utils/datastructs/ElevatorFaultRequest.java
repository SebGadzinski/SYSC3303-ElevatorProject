package project.utils.datastructs;

public class ElevatorFaultRequest extends Request {

    ElevatorFault elevatorFault;

    public ElevatorFaultRequest(SubsystemSource source, ElevatorFault elevatorFault) {
        super(source, 0);
        this.elevatorFault = elevatorFault;
    }

    public enum ElevatorFault {
        DOOR_FAULT, MOTOR_FAULT
	}

    public ElevatorFault getElevatorFaultFault() {
        return elevatorFault;
    }

    @Override
    public String toString() {
        return "ElevatorFaultRequest:\nFault: " + elevatorFault;
    }

}
