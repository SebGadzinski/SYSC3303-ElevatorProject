package project.utils.datastructs;

import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;

public class PersonRequest {

    private int originFloor;
    private int destinationFloor;
    private boolean originFloorCompleted;
    private boolean destinationFloorCompleted;
    private int fault;
    private boolean faultCompleted;

    public PersonRequest(int originFloor,
                         int destinationFloor,
                         boolean originFloorCompleted,
                         boolean destinationFloorCompleted) {

        this.originFloor = originFloor;
        this.destinationFloor = destinationFloor;
        this.originFloorCompleted = originFloorCompleted;
        this.destinationFloorCompleted = destinationFloorCompleted;
        this.faultCompleted = false;
        
    }
    
    public PersonRequest(int originFloor,
			            int destinationFloor,
			            boolean originFloorCompleted,
			            boolean destinationFloorCompleted, int fault) {
		this.originFloor = originFloor;
		this.destinationFloor = destinationFloor;
		this.originFloorCompleted = originFloorCompleted;
		this.destinationFloorCompleted = destinationFloorCompleted;
		this.fault = fault;
		this.faultCompleted = false;
    }

    public int getOriginFloor() {
        return originFloor;
    }

    public void setOriginFloor(int originFloor) {
        this.originFloor = originFloor;
    }

    public int getDestinationFloor() {
        return destinationFloor;
    }

    public void setDestinationFloor(int destinationFloor) {
        this.destinationFloor = destinationFloor;
    }

    public boolean isOriginFloorCompleted() {
        return originFloorCompleted;
    }

    public void setOriginFloorCompleted(boolean originFloorCompleted) {
        this.originFloorCompleted = originFloorCompleted;
    }

    public boolean isDestinationFloorCompleted() {
        return destinationFloorCompleted;
    }

    public void setDestinationFloorCompleted(boolean destinationFloorCompleted) {
        this.destinationFloorCompleted = destinationFloorCompleted;
    }
    
    public boolean isFaultCompleted() {
		return faultCompleted;
	}

	public void setFaultCompleted(boolean faultCompleted) {
		this.faultCompleted = faultCompleted;
	}

	public int getFault() {
		return fault;
	}

	public void setFault(int fault) {
		this.fault = fault;
	}

	@Override
    public String toString() {
        return "Request: " + "\n"
                + "destinationFloor: " + destinationFloor + "\n"
                + "destinationFloorCompleted: " + destinationFloorCompleted + "\n"
                + "originFloor: " + originFloor + "\n"
                + "originFloorCompleted: " + originFloorCompleted + "\n"
                + "fault: " + fault + "\n"
                + "faultCompleted: " + faultCompleted + "\n";
    }

}
