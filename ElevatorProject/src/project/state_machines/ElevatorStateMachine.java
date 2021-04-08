package project.state_machines;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import project.Config;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.ElevatorEmergencyRequest;
import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;
import project.utils.datastructs.ElevatorFaultRequest;
import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;
import project.utils.datastructs.ElevatorMotorRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.Request;
import project.utils.datastructs.SubsystemSource;
import project.utils.datastructs.SubsystemSource.Subsystem;

public class ElevatorStateMachine {
	private ElevatorState state;
	private ElevatorDoorStatus doorState;
	private ElevatorDirection directionState;
	private int currentFloor;
	private HashMap<Integer, Boolean> lamps;
	private Boolean motorFault, doorFault;

	public ElevatorStateMachine(ElevatorState state, ElevatorDoorStatus doorState, ElevatorDirection directionState,
			int currentFloor, HashMap<Integer, Boolean> lamps) {
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
	public Request handleRequest(Request request) {
		Request requestToSendToScheduler = null;
		if (request instanceof ElevatorEmergencyRequest) {
			ElevatorEmergencyRequest emergencyRequest = (ElevatorEmergencyRequest) request;
			requestToSendToScheduler = handleEmergencyRequest(emergencyRequest);
		}else {
			switch (state) {
			case IDLE -> {
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				} else if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				} else {
					System.out.print(request);
					System.out.println("Invalid Request For IDLE State");
				}
			}
			case ARRIVAL -> {
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				} else {
					System.out.println("Invalid Request For ARRIVAL State");
				}
			}
			case MOVING -> {
				if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					try {
						Thread.sleep(Config.elevator_time());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				} else {
					System.out.println("Invalid Request For MOVING State");
				}
			}
			case OPENING_DOORS -> {
				if (request instanceof ElevatorPassengerWaitRequest) {
					ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
					requestToSendToScheduler = handlePassengerWaitRequest(waitRequest);
				} else {
					System.out.println("Invalid Request For OPENING_DOORS State");
				}
			}
			case PASSENGER_HANDLING -> {
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
					requestToSendToScheduler = handleDoorRequest(doorRequest);
				} else {
					System.out.println("Invalid Request For PASSENGER_HANDLING State");
				}
			}
			case CLOSING_DOORS -> {
				if (request instanceof ElevatorMotorRequest) {
					ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
					requestToSendToScheduler = handleMotorRequest(motorRequest);
				} else {
					System.out.println("Invalid Request For CLOSING_DOORS State");
				}
			}
			// Fault cases are told to scheduler and taken care of using
			// ElevatorEmergencyRequest
			// It should not reach here. If so the fault has not been resolved
			case FAULT_HANDLING ->{ // System should shut down?
				System.out.println("CRITICAL FAULT");
			}
			}
		}
		return requestToSendToScheduler;
	}

	/**
	 * Overrides state for emergency purposes
	 *
	 * @param request The request to be dealt with.
	 * @return 
	 */
	private ElevatorEmergencyRequest handleEmergencyRequest(ElevatorEmergencyRequest request) {
		if (request.getEmergencyState() == ElevatorEmergency.FIX) {
			doorFault = false;
			motorFault = false;
			doorState = ElevatorDoorStatus.CLOSED;
			directionState = ElevatorDirection.IDLE;
			state = ElevatorState.IDLE;
			return new ElevatorEmergencyRequest(null, ElevatorEmergency.FIX, ElevatorEmergencyRequest.COMPLETED_EMERGENCY, doorState, directionState);
		}
		return null;
	}

	/**
	 * Handles a request to start motor and move up or down. Sends arrival request
	 * back once moved up/down a floor, or stopped at floor.
	 *
	 * @param request The request to be dealt with.
	 */
	public Request handleMotorRequest(ElevatorMotorRequest request) {
		// Stop sends a motorRequest letting the
		if (!motorFault) {
			if(!doorFault) {
				if (request.getRequestedDirection() == ElevatorDirection.IDLE) {
					setUpState(ElevatorDirection.IDLE, ElevatorState.ARRIVAL);
					setLampStatus(currentFloor, false);
					return new ElevatorDoorRequest(null, ElevatorDoorStatus.OPENED);
				} else if (request.getRequestedDirection() == ElevatorDirection.UP) {
						if(doorState == ElevatorDoorStatus.OPENED) {
							doorState = ElevatorDoorStatus.CLOSED;
							return doorFault();
						}
						if (currentFloor == Config.NUMBER_OF_FLOORS) {
							return new ElevatorArrivalRequest(null, currentFloor, directionState);
						}
						if (!motorFault) {
							setUpState(ElevatorDirection.UP, ElevatorState.MOVING);
							currentFloor += 1;
						} else {
							return motorFault();
						}
					
				} else {
						if(doorState == ElevatorDoorStatus.OPENED) {
							doorState = ElevatorDoorStatus.CLOSED;
							return doorFault(); 
						}
						if (currentFloor == 0) {
							return new ElevatorArrivalRequest(null, currentFloor, directionState);
						}
						if (!motorFault) {
							setUpState(ElevatorDirection.DOWN, ElevatorState.MOVING);
							waitForTime(Config.ELEVATOR_DOOR_TIME);
							currentFloor = currentFloor - 1;
						} else {
							return motorFault();
						}
					
				}
				return new ElevatorArrivalRequest(new SubsystemSource(Subsystem.ELEVATOR_SUBSYSTEM, ""), currentFloor,
						directionState);
			}else 
				return doorFault();
		}else 
			return motorFault();
	}

	/**
	 * Handles a request to open or close doors. Sends changed door request back to
	 * scheduler
	 *
	 * @param request The request to be dealt with.
	 */
	public Request handleDoorRequest(ElevatorDoorRequest request) {
		if (!doorFault) {
			if (request.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
				state = ElevatorState.CLOSING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.CLOSED;
				state = ElevatorState.IDLE;
				return new ElevatorDoorRequest(null, ElevatorDoorStatus.CLOSED);
			} else {
				state = ElevatorState.OPENING_DOORS;
				waitForTime(Config.ELEVATOR_DOOR_TIME);
				doorState = ElevatorDoorStatus.OPENED;
				return new ElevatorPassengerWaitRequest(null, Config.ELEVATOR_PASSENGER_WAIT_TIME, WaitState.WAITING);
			}
		} else {
			return doorFault();
		}
	}

	/**
	 * Handles a request to start motor and move up or down. Sets state to
	 * PassengerHandling and waits certain wait time
	 *
	 * @param request The request to be dealt with.
	 */
	public Request handlePassengerWaitRequest(ElevatorPassengerWaitRequest request) {
		state = ElevatorState.PASSENGER_HANDLING;

		if (motorFault) {
			return motorFault();
		}
		if (doorFault) {
			return doorFault();
		}

		// Imitate wait time
		waitForTime(request.getWaitTime());

		return new ElevatorPassengerWaitRequest(null, 0, WaitState.FINISHED);
	}

	/**
	 * Helper function that sets the state and direction state
	 *
	 * @param direction New direction status
	 * @param newState  New state status
	 */
	private void setUpState(ElevatorDirection direction, ElevatorState newState) {
		directionState = direction;
		state = newState;
	}

	/**
	 * Handler for a motor fault
	 */
	public Request motorFault() {
		state = ElevatorState.FAULT_HANDLING;
		return new ElevatorFaultRequest(null, ElevatorFault.MOTOR_FAULT);
	}

	/**
	 * Handler for a door fault
	 */
	public Request doorFault() {
		state = ElevatorState.FAULT_HANDLING;
		return new ElevatorFaultRequest(null, ElevatorFault.DOOR_FAULT);
	}

	/**
	 * Helper function that allows system to wait for the given duration.
	 *
	 * @param duration The amount of time to wait.
	 */
	public void waitForTime(int duration) {
		try {
			Thread.sleep(duration);
		} catch (java.lang.InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void setDoorFault() {
		this.doorFault = true;
	}

	public void setMotorFault() {
		this.doorFault = true;
	}

	/**
	 * Sets a lamps state, notifies other threads about the change
	 *
	 * @param floor  floor button lamp
	 * @param status status to be set
	 */
	public void setLampStatus(int floor, boolean status) {
		lamps.put(floor, status);
		// lamps.notifyAll();
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

	public enum ElevatorState {
		IDLE, ARRIVAL, MOVING, OPENING_DOORS, PASSENGER_HANDLING, CLOSING_DOORS, FAULT_HANDLING,
	}

	public enum ElevatorDoorStatus {
		OPENED, CLOSED
	}

	public enum ElevatorDirection {
		UP, DOWN, IDLE
	}

}
