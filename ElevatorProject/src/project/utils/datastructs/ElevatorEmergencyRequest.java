package project.utils.datastructs;

import project.state_machines.ElevatorStateMachine.ElevatorState;

public class ElevatorEmergencyRequest extends Request {
	public static int COMPLETED_EMERGENCY = 0;
	public static int INCOMPLETE_EMERGENCY = 1;
	private ElevatorEmergency emergencyState;
	private int status;
    
    public enum ElevatorEmergency {
    	FIX, SHUTDOWN
    }

	public ElevatorEmergencyRequest(SubsystemSource source, ElevatorEmergency emergencyState, int status) {
		super(source);
		this.emergencyState = emergencyState;
		this.status = status;
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
	
}
