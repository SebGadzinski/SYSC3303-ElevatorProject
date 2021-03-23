package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;

public class ElevatorEmergencyRequest extends Request {
	public static int COMPLETED_EMERGENCY = 0;
	public static int INCOMPLETE_EMERGENCY = 1;
	private ElevatorEmergency emergencyState;
	private int status;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
    
    public enum ElevatorEmergency {
    	FIX, SHUTDOWN
    }

	public ElevatorEmergencyRequest(SubsystemSource source, ElevatorEmergency emergencyState, int status, ElevatorDoorStatus doorState, ElevatorDirection directionState) {
		super(source);
		this.emergencyState = emergencyState;
		this.status = status;
		this.doorState = doorState;
		this.directionState = directionState;
	}

	public ElevatorEmergency getEmergencyState() {
		return emergencyState;
	}

	public void setEmergencyState(ElevatorEmergency emergencyState) {
		this.emergencyState = emergencyState;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public ElevatorDoorStatus getDoorState() {
		return doorState;
	}

	public void setDoorState(ElevatorDoorStatus doorState) {
		this.doorState = doorState;
	}

	public ElevatorDirection getDirectionState() {
		return directionState;
	}

	public void setDirectionState(ElevatorDirection directionState) {
		this.directionState = directionState;
	}

	@Override
	public String toString() {
		return "ElevatorEmergencyRequest emergencyState: " + emergencyState + "\n code: " + status + " doors: "
				+ doorState + ", direction: " + directionState + "\n";
	}
	
	
	
	
	
}
