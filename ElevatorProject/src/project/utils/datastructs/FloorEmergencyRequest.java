package project.utils.datastructs;

import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;

public class FloorEmergencyRequest extends Request{
	private FloorEmergency emergencyState;
	
    public enum FloorEmergency {
    	FIX, SHUTDOWN
    }
	public FloorEmergencyRequest(SubsystemSource source, FloorEmergency emergencyState, int fault) {
		super(source, fault);
		this.emergencyState = emergencyState;
	}
	
	public FloorEmergencyRequest(SubsystemSource source, FloorEmergency emergencyState) {
		super(source, 0);
		this.emergencyState = emergencyState;
	}

	public FloorEmergency getEmergencyState() {
		return emergencyState;
	}

	public void setEmergencyState(FloorEmergency emergencyState) {
		this.emergencyState = emergencyState;
	}

	@Override
	public String toString() {
		return "FloorEmergencyRequest emergencyState: " + emergencyState;
	}

	
	
}
