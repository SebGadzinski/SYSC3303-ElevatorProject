package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class ElevatorDoorRequest extends Request {

    private ElevatorDoorStatus requestedDoorStatus;

    public ElevatorDoorRequest(SubsystemSource source, ElevatorDoorStatus requestedDoorStatus) {
        super(source, 0);
        this.requestedDoorStatus = requestedDoorStatus;
    } 
	public ElevatorDoorRequest(SubsystemSource source, ElevatorDoorStatus requestedDoorStatus, int fault) {
		super(source, fault);
        this.requestedDoorStatus = requestedDoorStatus;
    }
    @Override
    public String toString() {
        return "ElevatorDoorRequest:\nRequest for doors to be: " + requestedDoorStatus + "\n" +
        		(this.getFault() > 0 ? ("Fault: " + this.getFault()) : "");
    }

    public ElevatorDoorStatus getRequestedDoorStatus() {
        return requestedDoorStatus;
    }

    public void setRequestedDoorStatus(ElevatorDoorStatus requestedDoorStatus) {
        this.requestedDoorStatus = requestedDoorStatus;
    }

}
