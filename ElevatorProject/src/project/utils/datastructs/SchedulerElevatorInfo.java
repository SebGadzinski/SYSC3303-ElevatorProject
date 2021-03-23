package project.utils.datastructs;

import java.util.ArrayList;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;

public class SchedulerElevatorInfo extends SchedulerSubsystemInfo {

    private ElevatorDirection direction;
    private ElevatorDoorStatus doorStatus;
    private ArrayList<PersonRequest> requests;
    private int currentDestinationFloor;
    private int passengers;
    private int currentFloor;
    private Thread timerWorker;
    private Boolean timeOut;
    private ElevatorTimerWorker timer;

    public SchedulerElevatorInfo(String id, UDPInfo udpInfo, ElevatorDirection direction,
                                 ElevatorDoorStatus doorStatus, int currentFloor) {
        super(id, udpInfo);
        this.direction = direction;
        this.doorStatus = doorStatus;
        this.currentFloor = currentFloor;
        this.currentDestinationFloor = -1;
        this.passengers = 0;
        this.timeOut = false;        
        this.timer = new ElevatorTimerWorker();
        this.timerWorker = new Thread(this.timer, "timer");
        
        requests = new ArrayList<>();
    }

    /**
     * Replicates this Elevators Subsystem identification
     *
     * @return Elevator subsystems identification
     */
    public SubsystemSource getSource() {
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

    public void setPersonRequestOriginFloor(int index, int newFloor) {
        requests.get(index).setOriginFloor(newFloor);
    }

    public void setPersonRequestDestinationFloor(int index, int newFloor) {
        requests.get(index).setDestinationFloor(newFloor);
    }

    public void setPersonRequestDestinationFloorCompleted(int index, boolean completed) {
        requests.get(index).setDestinationFloorCompleted(completed);
    }

    public void setPersonRequestOriginFloorCompleted(int index, boolean completed) {
        requests.get(index).setOriginFloorCompleted(completed);
    }

    public int getCurrentDestinationFloor() {
        return currentDestinationFloor;
    }

    public void setCurrentDestinationFloor(int currentDestinationFloor) {
        this.currentDestinationFloor = currentDestinationFloor;
    }

    public PersonRequest removeFromRequests(int index) {
        return requests.remove(index);
    }

    public void addToRequests(PersonRequest request) {
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

    public synchronized void startTimer() {
    	if(this.timerWorker.getState() == Thread.State.NEW) {
        	this.timerWorker.start();
    	}
    	else if (this.timerWorker.getState() == Thread.State.TERMINATED) {
    		this.timer = new ElevatorTimerWorker();
    		this.timerWorker = new Thread(this.timer, "timer");
    		this.timerWorker.start();
    	}    	
    }
    
    public synchronized void stopTimer() {
    	this.timerWorker.interrupt();
    	while(this.timerWorker.getState() != Thread.State.TERMINATED) {};
    }
    
    public synchronized boolean isTimerRunning() {
    	return this.timer.getTimerRunning();
    }
    
    public synchronized boolean getTimerRunning() {
    	return this.timer.getTimerRunning();
    }
    
    public synchronized boolean getTimeOut() {
    	return this.timer.getTimeOut();
    }
    
    @Override
    public String toString() {
        return "Elevator: " + "\n"
                + "id: " + getId() + "\n"
                + getUdpInfo() + "\n"
                + "# of passengers: " + passengers + "\n"
                + "direction: " + direction + "\n"
                + "currentFloor: " + currentFloor + "\n"
                + "destinationFloor: " + currentDestinationFloor + "\n"
                + "doorStatus: " + doorStatus;
    }

    /**
     * Gets closest PersonRequest to this elevator
     *
     * @return Closest PersonRequest to this elevator
     */
    public PersonRequest closestRequest() {
        if (requests.size() > 0) {
            PersonRequest closestRequest = requests.get(0);

            for (int i = 1; i < requests.size(); i++) {
                if (!requests.get(i).isOriginFloorCompleted()) {
                    // If the origin floor is closest
                    if (Math.abs(currentFloor - requests.get(i).getOriginFloor()) < Math.abs(currentFloor - closestRequest.getOriginFloor())) {
                        closestRequest = requests.get(i);
                    }
                }
                if (!requests.get(i).isDestinationFloorCompleted() && requests.get(i).isOriginFloorCompleted()) {
                    if (Math.abs(currentFloor - requests.get(i).getDestinationFloor()) < Math.abs(currentFloor - closestRequest.getDestinationFloor())) {
                        closestRequest = requests.get(i);
                    }
                }
            }
            return closestRequest;
        }
        return null;
    }

}
