package project.utils.datastructs;

public class PersonRequest {
    private int originFloor;
    private int destinationFloor;
    private boolean originFloorCompleted;
    private boolean destinationFloorCompleted;
    
    public PersonRequest(int orginFloor, int destinationFloor, boolean orginFloorCompleted,
            boolean destinationFloorCompleted) {
        this.originFloor = orginFloor;
        this.destinationFloor = destinationFloor;
        this.originFloorCompleted = orginFloorCompleted;
        this.destinationFloorCompleted = destinationFloorCompleted;
    }
    public int getOrginFloor() {
        return originFloor;
    }
    public void setOriginFloor(int orginFloor) {
        this.originFloor = orginFloor;
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
    public void setOriginFloorCompleted(boolean orginFloorCompleted) {
        this.originFloorCompleted = orginFloorCompleted;
    }
    public boolean isDestinationFloorCompleted() {
        return destinationFloorCompleted;
    }
    public void setDestinationFloorCompleted(boolean destinationFloorCompleted) {
        this.destinationFloorCompleted = destinationFloorCompleted;
    }
    @Override
    public String toString() {
        return "Request: " + "\n:"
        + "destinationFloor: " + destinationFloor + "\n"
        + "destinationFloorCompleted: " + destinationFloorCompleted + "\n"
        + "originFloor: " + originFloor + "\n"
        + "originFloorCompleted: " + originFloorCompleted+ "\n";
    }

}
