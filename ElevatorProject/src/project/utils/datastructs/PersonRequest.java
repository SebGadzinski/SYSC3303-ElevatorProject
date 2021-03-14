package project.utils.datastructs;

public class PersonRequest {

    private int originFloor;
    private int destinationFloor;
    private boolean originFloorCompleted;
    private boolean destinationFloorCompleted;

    public PersonRequest(int originFloor,
                         int destinationFloor,
                         boolean originFloorCompleted,
                         boolean destinationFloorCompleted) {

        this.originFloor = originFloor;
        this.destinationFloor = destinationFloor;
        this.originFloorCompleted = originFloorCompleted;
        this.destinationFloorCompleted = destinationFloorCompleted;

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

    @Override
    public String toString() {
        return "Request: " + "\n"
                + "destinationFloor: " + destinationFloor + "\n"
                + "destinationFloorCompleted: " + destinationFloorCompleted + "\n"
                + "originFloor: " + originFloor + "\n"
                + "originFloorCompleted: " + originFloorCompleted + "\n";
    }

}
