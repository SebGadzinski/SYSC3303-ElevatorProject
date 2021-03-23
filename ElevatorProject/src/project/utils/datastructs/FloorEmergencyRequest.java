package project.utils.datastructs;

import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;

public class FloorEmergencyRequest extends Request{
	private FloorEmergency emergencyState;
	
    public enum FloorEmergency {
    	FIX, SHUTDOWN
    }
	public FloorEmergencyRequest(SubsystemSource source, FloorEmergency emergencyState) {
		super(source);
		this.emergencyState = emergencyState;
	}

	public FloorEmergency getEmergencyState() {
		return emergencyState;
	}

	public void setEmergencyState(FloorEmergency emergencyState) {
		this.emergencyState = emergencyState;
	}

	
}
