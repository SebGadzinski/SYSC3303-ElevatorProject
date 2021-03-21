package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class ElevatorDoorRequest extends Request {

    private ElevatorDoorStatus requestedDoorStatus;

    public ElevatorDoorRequest(SubsystemSource source, ElevatorDoorStatus requestedDoorStatus) {
        super(source);
        this.requestedDoorStatus = requestedDoorStatus;
    }

    @Override
    public String toString() {
        return "ElevatorDoorRequest:\nRequest for doors to be: " + requestedDoorStatus + "\n";
    }

    public ElevatorDoorStatus getRequestedDoorStatus() {
        return requestedDoorStatus;
    }

    public void setRequestedDoorStatus(ElevatorDoorStatus requestedDoorStatus) {
        this.requestedDoorStatus = requestedDoorStatus;
    }

}
