package project.state_machines;

import java.util.HashMap;

import project.Config;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.ElevatorEmergencyRequest;
import project.utils.datastructs.ElevatorFaultRequest;
import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;
import project.utils.datastructs.ElevatorMotorRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.Request.Source;

public class ElevatorStateMachine {
	
	private ElevatorState state;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
	private int currentFloor;
	private HashMap<Integer, Boolean> lamps;
	private Boolean motorFault, doorFault;

	
	public ElevatorStateMachine(ElevatorState state, ElevatorDoorStatus doorState,
			ElevatorDirection directionState, int currentFloor, HashMap<Integer, Boolean> lamps) {
		super();
		this.state = state;
		this.doorState = doorState;
		this.directionState = directionState;
		this.currentFloor = currentFloor;
		this.lamps = lamps;
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
			case ELEVATOR_ARRIVAL ->{
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				}
				else {
					System.out.println("Invalid Request For ELEVATOR_ARRIVAL State");
				}
			}
			case ELEVATOR_MOVING ->{
					if (request instanceof ElevatorMotorRequest) {
						ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
						requestToSendToScheduler = handleMotorRequest(motorRequest);
					}
					else {
						System.out.println("Invalid Request For MOVING State");
					}
			}
			case ELEVATOR_OPENING_DOORS ->{
				if (request instanceof ElevatorPassengerWaitRequest) {
					ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
					requestToSendToScheduler = handlePassengerWaitRequest(waitRequest);
				} 
				else {
					System.out.println("Invalid Request For ELEVATOR_OPENING_DOORS State");
				}
			}
			case ELEVATOR_CLOSING_DOORS ->{
				if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				}
				else {
					System.out.println("Invalid Request For ELEVATOR_CLOSING_DOORS State");
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
		}
		System.out.print("State: " + state);
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
				setUpState(ElevatorDirection.IDLE, ElevatorState.ELEVATOR_ARRIVAL);
				setLampStatus(currentFloor, false);
			}else if (request.getRequestedDirection() == ElevatorDirection.UP) {
				System.out.println("Moving Elevator Up");
				if (currentFloor == Config.NUMBER_OF_FLOORS) {
					System.out.println(
							"Currently at max floor. Motor request denied. \n Sending a arrival request on max floor");
					return new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM, currentFloor, directionState); 
				}
				if(motorFault){
					setUpState(ElevatorDirection.UP, ElevatorState.ELEVATOR_MOVING);
					waitForTime(Config.ELEVATOR_DOOR_TIME);
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
							currentFloor, directionState);
				}
				if(!motorFault){
					setUpState(ElevatorDirection.DOWN, ElevatorState.ELEVATOR_MOVING);
					waitForTime(Config.ELEVATOR_DOOR_TIME);
					currentFloor = currentFloor + 1;
				}else{
					return motorFault();
				}
		}
		return new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM, currentFloor, directionState);
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
				state = ElevatorState.ELEVATOR_CLOSING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.CLOSED;
			} else {
				System.out.println("Opening Doors");
				state = ElevatorState.ELEVATOR_OPENING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.OPENED;
			}
			return new ElevatorDoorRequest(Source.ELEVATOR_SUBSYSTEM, doorState);
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

    public void setLampStatus(int floor, boolean status){
		lamps.put(floor, status);
		notifyAll();
	}
	
	public Request motorFault(){
		motorFault = false;
		return new ElevatorFaultRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorFault.MOTOR_FAULT);
	}

	public Request doorFault(){
		doorFault = false;
		return new ElevatorFaultRequest(Source.ELEVATOR_SUBSYSTEM, ElevatorFault.DOOR_FAULT);
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

	public enum ElevatorState{
		IDLE,
		ELEVATOR_ARRIVAL,
		ELEVATOR_MOVING,
		ELEVATOR_OPENING_DOORS,
		ELEVATOR_CLOSING_DOORS,
		PASSENGER_HANDLING,
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


