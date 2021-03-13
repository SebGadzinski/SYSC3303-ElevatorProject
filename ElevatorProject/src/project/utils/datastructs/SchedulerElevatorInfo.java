package project.utils.datastructs;

import java.util.ArrayList;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.SubsystemSource;

public class SchedulerElevatorInfo extends AbstractSchedulerSubSystemInfo{
    private ElevatorDirection direction;
    private ElevatorDoorStatus doorStatus;
    private ArrayList<PersonRequest> requests;
    private int currentDestinationFloor;
    private int passengers;
    private int currentFloor;
    public SchedulerElevatorInfo(String id, UDPInfo udpInfo, ElevatorDirection direction,
            ElevatorDoorStatus doorStatus, int currentFloor) {
        super(id, udpInfo);
        this.direction = direction;
        this.doorStatus = doorStatus;
        this.currentFloor = currentFloor;
        this.currentDestinationFloor = -1;
        this.passengers = 0;
        requests = new ArrayList<PersonRequest>();
    }

    /**
     * Replicates this Elevators Subsystem identification
     * 
     * @return Elevator subsystems identification
     */
    public SubsystemSource getSource(){
        return new SubsystemSource(SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM, getId());
    }

    public int getNumberOfRequests() {
        return requests.size();
    }
    public ElevatorDirection getDirection() {
        return direction;
    }
    public void setDirection(ElevatorDirection direction) {
        this.direction = direction;
    }
    public ElevatorDoorStatus getDoorStatus() {
        return doorStatus;
    }
    public void setDoorStatus(ElevatorDoorStatus doorStatus) {
        this.doorStatus = doorStatus;
    }
    public ArrayList<PersonRequest> getRequests() {
        return requests;
    }
    public void setRequests(ArrayList<PersonRequest> orders) {
        this.requests = orders;
    }
    public int getCurrentFloor() {
        return currentFloor;
    }
    public void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
    }
    public void setPersonRequestOriginFloor(int index, int newFloor){
        requests.get(index).setOriginFloor(newFloor);
    }
    public void setPersonRequestDestinationFloor(int index, int newFloor){
        requests.get(index).setDestinationFloor(newFloor);
    }
    public void setPersonRequestDestinationFloorCompleted(int index, boolean completed){
        requests.get(index).setDestinationFloorCompleted(completed);
    }
    public void setPersonRequestOriginFloorCompleted(int index, boolean completed){
        requests.get(index).setOriginFloorCompleted(completed);
    }
    public int getCurrentDestinationFloor() {
        return currentDestinationFloor;
    }
    public void setCurrentDestinationFloor(int currentDestinationFloor) {
        this.currentDestinationFloor = currentDestinationFloor;
    }
    public PersonRequest removeFromRequests(int index){
        return requests.remove(index);
    }
    public void addToRequests(PersonRequest request){
        requests.add(request);
    }
    public int getPassengers() {
        return passengers;
    }
    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
    public void addPassenger() {
        this.passengers++;
    }
    public void subtractPassenger() {
        this.passengers--;
    }

    @Override
    public String toString() {
        return "Elevator: " + "\n"
        + "id: " + getId() + "\n"
        + getUdpInfo().toString() + "\n" 
        + "# of passengers: " + passengers + "\n"
        + "direction: " + direction + "\n"
        + "currentFloor: " + currentFloor + "\n"
        + "destination: " + currentDestinationFloor + "\n"
        + "door status: " + doorStatus;
    }

    /**
     * Gets closest PersonRequest to this elevator
     * 
     * @return Closest PersonRequest to this elevator
     */
    public PersonRequest closestRequest() {
        if(requests.size() > 0){
            PersonRequest closestRequest = requests.get(0);

            for(int i = 1; i < requests.size(); i++){
                if(!requests.get(i).isOriginFloorCompleted()){
                    //If the orgin floor is closest
                    if(Math.abs(currentFloor - requests.get(i).getOrginFloor()) < Math.abs(currentFloor - closestRequest.getOrginFloor())){
                        closestRequest = requests.get(i);
                    }
                }
                if(!requests.get(i).isDestinationFloorCompleted() && requests.get(i).isOriginFloorCompleted()){
                    if(Math.abs(currentFloor - requests.get(i).getDestinationFloor()) < Math.abs(currentFloor - closestRequest.getDestinationFloor())){
                        closestRequest = requests.get(i);
                    }
                }
            }
            return closestRequest;
        }
        return null;
    }



    

}
