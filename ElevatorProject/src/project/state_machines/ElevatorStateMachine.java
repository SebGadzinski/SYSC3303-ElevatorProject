package project.state_machines;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import project.Config;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.ElevatorEmergencyRequest;
import project.utils.datastructs.ElevatorFaultRequest;
import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;
import project.utils.datastructs.ElevatorMotorRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.Request.Source;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ElevatorStateMachine {
	
	private ElevatorState state;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
	private int currentFloor;
	private HashMap<Integer, Boolean> lamps;
	private Queue<FileRequest> destinationFloors;
	private Boolean motorFault, doorFault;

	
	public ElevatorStateMachine(ElevatorState state, ElevatorDoorStatus doorState,
			ElevatorDirection directionState, int currentFloor, HashMap<Integer, Boolean> lamps) {
		super();
		this.state = state;
		this.doorState = doorState;
		this.directionState = directionState;
		this.currentFloor = currentFloor;
		this.lamps = lamps;
		this.destinationFloors = new LinkedList<FileRequest>();
		motorFault = false;
		doorFault = false;
	}

	/**
	 * Changes the request based off current state and request
	 * 
	 * @param request The request to be dealt with.
	 */
	public Request handleRequest(Request request){
		Request requestToSendToScheduler = null;
		if(request instanceof ElevatorEmergencyRequest) {
			ElevatorEmergencyRequest emergencyRequest = (ElevatorEmergencyRequest) request;
			handleEmergencyRequest(emergencyRequest);
			request = emergencyRequest.getEmergencyRequest();
		}
		switch (state){
			case IDLE ->{
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				}
				else if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				}
				else {
					System.out.println("Invalid Request For IDLE State");
				}
			}
			case ARRIVAL ->{
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				}
				else {
					System.out.println("Invalid Request For ARRIVAL State");
				}
			}
			case MOVING ->{
					if (request instanceof ElevatorMotorRequest) {
						ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
						requestToSendToScheduler = handleMotorRequest(motorRequest);
					}
					else {
						System.out.println("Invalid Request For MOVING State");
					}
			}
			case OPENING_DOORS ->{
				if (request instanceof ElevatorPassengerWaitRequest) {
					ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
					requestToSendToScheduler = handlePassengerWaitRequest(waitRequest);
				} 
				else {
					System.out.println("Invalid Request For OPENING_DOORS State");
				}
			}
			case PASSENGER_HANDLING ->{
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				}
				else {
					System.out.println("Invalid Request For PASSENGER_HANDLING State");
				}
			}
			case CLOSING_DOORS ->{
				if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				}
				else {
					System.out.println("Invalid Request For CLOSING_DOORS State");
				}
			}
			//Fault cases are told to scheduler and taken care of using ElevatorEmergencyRequest
			//It should not reach here. If so the fault has not been resolved
			case FAULT_HANDLING ->{
				System.out.println("CRITICAL FAULT");
				//System should shut down?
			}
		}
		System.out.print("State: " + state + "\n");
		return requestToSendToScheduler;
	}

	/**
	 * Overrides state for emergency purposes
	 * 
	 * @param request The request to be dealt with.
	 */
	private void handleEmergencyRequest(ElevatorEmergencyRequest request) {
		state = request.getEmergencyState();
	}

	/**
	 * Handles a request to start motor and move up or down. Sends arrival request
	 * back once moved up/donw a floor, or stopped at floor.
	 * 
	 * @param request The request to be dealt with.
	 */
    public Request handleMotorRequest(ElevatorMotorRequest request) {
		//Stop sends a motorRequest letting the 
		if(!motorFault){
			if (request.getRequestedDirection() == ElevatorDirection.IDLE) {
				System.out.println("Stopping Elevator");
				setUpState(ElevatorDirection.IDLE, ElevatorState.ARRIVAL);
				setLampStatus(currentFloor, false);
				return new ElevatorDoorRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorDoorStatus.OPENED);
			}else if (request.getRequestedDirection() == ElevatorDirection.UP) {
				System.out.println("Moving Elevator Up");
				if (currentFloor == Config.NUMBER_OF_FLOORS) {
					System.out.println(
							"Currently at max floor. Motor request denied. \n Sending a arrival request on max floor");
					return new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM, currentFloor, directionState, destinationFloors.peek().getDestinationFloor()); 
				}
				if(!motorFault){
					setUpState(ElevatorDirection.UP, ElevatorState.MOVING);
					//waitForTime(Config.ELEVATOR_DOOR_TIME);
					currentFloor += 1;
				}
				else{
					return motorFault();
				}
			} else {
				System.out.println("Moving Elevator Down");
				if (currentFloor == 0) {
					System.out.println(
							"Currently at basement floor. Motor request denied. \n Sending a arrival request on basement floor");
							return new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM,
							currentFloor, directionState, destinationFloors.peek().getDestinationFloor());
				}
				if(!motorFault){
					setUpState(ElevatorDirection.DOWN, ElevatorState.MOVING);
					waitForTime(Config.ELEVATOR_DOOR_TIME);
					currentFloor = currentFloor + 1;
				}else{
					return motorFault();
				}
		}
		return new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM, currentFloor, directionState, destinationFloors.peek().getDestinationFloor());
		}
		else{
			motorFault = false;
			return new ElevatorFaultRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorFault.MOTOR_FAULT);
		}
	}
	
	/**
     * Handles a request to open or close doors. 
     * Sends changed door request back to schedular
     *
     * @param request The request to be dealt with.
     */
    public Request handleDoorRequest(ElevatorDoorRequest request) {
		if(!doorFault){
			if (request.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
				System.out.println("Closing Doors");
				state = ElevatorState.CLOSING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.CLOSED;
				FileRequest arrivedFileRequest = popDestinationQueue();
				if (arrivedFileRequest != null)
					return new FileRequest(getTimeStamp(), arrivedFileRequest.getOriginFloor(), directionState, arrivedFileRequest.getDestinationFloor(), Source.ELEVATOR_SUBSYSTEM);
				else return null;
			} else {
				System.out.println("Opening Doors");
				state = ElevatorState.OPENING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.OPENED;
				return new ElevatorPassengerWaitRequest(Source.ELEVATOR_SUBSYSTEM, Config.ELEVATOR_PASSENGER_WAIT_TIME, WaitState.WAITING);
			}
		}
		else{
			return doorFault();
		}
	}
	
	/**
     * Handles a request to start motor and move up or down. 
     * Sets state to PassengerHandling and waits certain wait time
     * 
     * @param request The request to be dealt with.
     */
    public Request handlePassengerWaitRequest(ElevatorPassengerWaitRequest request) {
		state = ElevatorState.PASSENGER_HANDLING;
		
		if(motorFault){
			return motorFault();
		}
		if(doorFault){
			return doorFault();
		}

        //Imitate wait time
		waitForTime(request.getWaitTime());
		
		return new ElevatorPassengerWaitRequest(Source.ELEVATOR_SUBSYSTEM, 0, WaitState.FINISHED);
	}
	
	/**
     * Helper function that sets the state and direciton state
     * 
     * @param direction New direction status
     * @param newState New state status
     */
    private void setUpState(ElevatorDirection direction, ElevatorState newState){
        directionState = direction;
        state = newState;
    }
	
	/**
     * Handler for a motor fault
     */
	public Request motorFault(){
		state = ElevatorState.FAULT_HANDLING;
		motorFault = false;
		return new ElevatorFaultRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorFault.MOTOR_FAULT);
	}

	/**
     * Handler for a door fault
     */
	public Request doorFault(){
		state = ElevatorState.FAULT_HANDLING;
		doorFault = false;
		return new ElevatorFaultRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorFault.DOOR_FAULT);
	}

	/**
     * Add floor to destinations of queue
     */
	public void putDestinationQueue(FileRequest request){
		if (request.getDestinationFloor() <= Config.NUMBER_OF_FLOORS && request.getDestinationFloor() >= 0){
			destinationFloors.add(request);
		}
	}
	
	public boolean noMoreDestinations(){
		return destinationFloors.isEmpty();
	}
	/**
     * Pop top of queue and return it
     */
	public FileRequest popDestinationQueue(){
		return destinationFloors.remove();
	}

	/**
     * Helper function That allows system to wait for the duration given
     * 
     * @param request The request to be dealt with.
     */
    public void waitForTime(int duration){
        try{
            Thread.sleep(duration);
        }catch (java.lang.InterruptedException e) {
            e.printStackTrace();
        }
	}

	/**
     * Get current time stamp
     * 
     */
	public String getTimeStamp(){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");  
        Date date = new Date();  
        return formatter.format(date);  
    }

	/**
     * Sets a lamps state, notifies other threads about the change
     * 
     * @param floor floor button lamp
	 * @param status status to be set
     */
	public void setLampStatus(int floor, boolean status){
		lamps.put(floor, status);
		//lamps.notifyAll();
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

	public int getCurrentFloor() {
		return currentFloor;
	}

	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}

	public ElevatorState getState() {
		return state;
	}

	public void setState(ElevatorState state) {
		this.state = state;
	}

	public ElevatorDoorStatus getDoorState() {
		return doorState;
	}

	public enum ElevatorState{
		IDLE,
		ARRIVAL,
		MOVING,
		OPENING_DOORS,
		PASSENGER_HANDLING,
		CLOSING_DOORS,
		FAULT_HANDLING,
	}

	public enum ElevatorDoorStatus{
		OPENED, 
		CLOSED
	}

	public enum ElevatorDirection{
		UP, 
		DOWN, 
		IDLE
	}
	
}


