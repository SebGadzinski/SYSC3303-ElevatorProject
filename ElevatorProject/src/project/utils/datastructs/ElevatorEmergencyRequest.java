package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorState;

public class ElevatorEmergencyRequest extends Request {

    ElevatorState emergencyState;
    Request emergencyRequest;

    public ElevatorEmergencyRequest(SubsystemSource source, ElevatorState emergencyState, Request emergencyRequest) {
        super(source);
        this.emergencyState = emergencyState;
        this.emergencyRequest = emergencyRequest;
    }

    public ElevatorState getEmergencyState() {
        return emergencyState;
    }

    public Request getEmergencyRequest() {
        return emergencyRequest;
    }

    @Override
    public String toString() {
        return "ElevatorEmergencyRequest:\nEmergencyRequest: " + emergencyRequest + "\nEmergencyState: " + emergencyState + "\n";
    }

}
