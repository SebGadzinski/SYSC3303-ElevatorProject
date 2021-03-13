package project.systems;

import static project.Config.SCHEDULER_UDP_INFO;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.ElevatorDestinationRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.ElevatorEmergencyRequest;
import project.utils.datastructs.ElevatorFaultRequest;
import project.utils.datastructs.ElevatorMotorRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.PersonRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.SchedulerElevatorInfo;
import project.utils.datastructs.SchedulerFloorInfo;
import project.utils.datastructs.SubsystemSource;
import project.utils.objects.general.CreateFile;

/**
 * A request/response transmission intermediary for elevator and floor
 * subsystems; schedules and coordinates elevators in optimally servicing
 * passenger requests.
 *
 * @author Paul Roode (iter 3 and 2), Sebastian Gadzinski (iter 1, iter 3)
 * @version Iteration 3
 */

public class Scheduler extends AbstractSubsystem implements Runnable {

	private SchedulerState state;
	private List<SchedulerElevatorInfo> elevators;
	private List<SchedulerFloorInfo> floors;
	private CreateFile file;

	/**
	 * A parameterized Scheduler constructor that initializes all fields, including
	 * those inherited.
	 *
	 * @param inetAddress   The Scheduler's IP address.
	 * @param inSocketPort  The Scheduler's inlet socket port number.
	 * @param outSocketPort The Scheduler's outlet socket port number.
	 */
	public Scheduler(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
		super(inetAddress, inSocketPort, outSocketPort);
		file = new CreateFile("schedulerFile.txt");
		state = SchedulerState.AWAIT_REQUEST;
		elevators = new ArrayList<SchedulerElevatorInfo>();
		for (int i = 0; i < project.Config.NUMBER_OF_ELEVATORS; i++) {
			elevators.add(new SchedulerElevatorInfo(Integer.toString(i), project.Config.ELEVATORS_UDP_INFO[i],
					ElevatorDirection.IDLE, ElevatorDoorStatus.CLOSED, 0));
		}
		floors = new ArrayList<SchedulerFloorInfo>();
		for (int i = 0; i < project.Config.NUMBER_OF_FLOORS; i++) {
			floors.add(new SchedulerFloorInfo(Integer.toString(i), project.Config.FLOORS_UDP_INFO[i]));
		}
	}

	/**
	 * Dispatches the given Request to the given ElevatorSubsystem.
	 *
	 * @param request           The Request to be dispatched.
	 * @param elevatorSubsystem The ElevatorSubsystem to which the Request will be
	 *                          dispatched.
	 */
	public synchronized void dispatchRequestToElevatorSubsystem(Request request, SchedulerElevatorInfo elevatorInfo) {
		request.setSource(elevatorInfo.getSource());
		sendRequest(request, elevatorInfo.getUdpInfo().getInetAddress(), elevatorInfo.getUdpInfo().getInSocketPort());
		file.writeToFile(this + " says:");

		file.writeToFile(this + " says:");
		file.writeToFile(this + " sent a request to " + elevatorInfo);
		file.writeToFile(request.toString());
	}

	/**
	 * Dispatches the given Request to the given FloorSubsystem.
	 *
	 * @param request        The Request to be dispatched.
	 * @param floorSubsystem The FloorSubsystem to which the Request will be
	 *                       dispatched.
	 */
	public synchronized void dispatchRequestToFloorSubsystem(Request request, SchedulerFloorInfo floorInfo) {
		request.setSource(getSource());
		sendRequest(request, floorInfo.getUdpInfo().getInetAddress(), floorInfo.getUdpInfo().getInSocketPort());
		file.writeToFile(this + " says:");
		file.writeToFile(this + " sent a request to " + floorInfo);
		file.writeToFile(request.toString());
	}

	/**
	 * Fetches a Request, waiting if necessary until one becomes available; then
	 * advances this Scheduler's state.
	 *
	 * @return the fetched Request.
	 */
	public synchronized Request fetchRequest() {

		Request request = waitForRequest();

		// printing
		file.writeToFile(this + " says:");
		file.writeToFile("Request received by " + this + " from " + request.getSource());
		file.writeToFile(request.toString());

		// Receive message
		// sendRequest(request, elevatorInfo.getUdpInfo().getInetAddress(),
		// elevatorInfo.getUdpInfo().getInSocketPort());

		advanceState(request);
		return request;

	}

	/**
	 * Processes the given Request and issues commands accordingly, then advances
	 * this Scheduler's state.
	 *
	 * @param request The Request to be processed.
	 */
	private synchronized void consumeRequest(Request request) {
		switch (state) {
		case DISPATCH_REQUEST_TO_SUBSYSTEM -> {
			// If a current destination for the elevator has already been picked, add it to
			// the list requests, otherise send it to elevatorSubsystem
			if (request instanceof FileRequest) {
				FileRequest fileRequest = (FileRequest) request;
				SchedulerElevatorInfo elevator = selectElevator(fileRequest);
				PersonRequest personRequest = new PersonRequest(fileRequest.getOriginFloor(),
						fileRequest.getDestinationFloor(), false, false);

				elevator.addToRequests(personRequest);
				file.writeToFile("Added Request to list of requests to: \n" + elevator.getSource());

				if (elevator.getCurrentDestinationFloor() == -1) {
					elevator.setCurrentDestinationFloor(fileRequest.getOriginFloor());
					dispatchRequestToElevatorSubsystem(fileRequest, elevator);
				}
			}
			if (request.getSource().getSubsystem() == SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM) {
				SchedulerElevatorInfo elevator = elevators.get(Integer.parseInt(request.getSource().getId()));

				// If anybody has a request on this floor open the doors, otherwise go towards
				// destination
				if (request instanceof ElevatorDestinationRequest) {
					ElevatorDestinationRequest destinationRequest = (ElevatorDestinationRequest) request;

					if (elevator.getCurrentDestinationFloor() != -1) {
						ElevatorDirection direction = getDirectionFromFloor(elevator.getCurrentDestinationFloor(),
								elevator);
						if (direction == ElevatorDirection.IDLE) {
							elevator.setCurrentDestinationFloor(-1);
						}
						// Check if anyone needs to be dropped off on this floor
						if (needToOpenDoors(elevator)) {
							dispatchRequestToFloorSubsystem(
									new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED),
									floors.get(elevator.getCurrentFloor()));
							dispatchRequestToElevatorSubsystem(
									new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED), elevator);
						} else {
							dispatchRequestToElevatorSubsystem(
									new ElevatorMotorRequest(getSource(),
											getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator)),
									elevator);
						}
					} else {
						if (elevator.getRequests().size() > 0) {
							PersonRequest closestRequest = elevator.closestRequest();
							elevator.setCurrentDestinationFloor(
									closestRequest.isOriginFloorCompleted() ? closestRequest.getDestinationFloor()
											: closestRequest.getOrginFloor());
							handlePersonRequest(closestRequest, elevator);
						} else {
							elevator.setCurrentDestinationFloor(-1);
						}
					}
				}
				// If anybody has a request on this floor open the doors, otherwise go towards
				// destination
				if (request instanceof ElevatorArrivalRequest) {
					ElevatorArrivalRequest arrivalRequest = (ElevatorArrivalRequest) request;
					ArrayList<PersonRequest> requests = elevator.getRequests();

					elevator.setDirection(arrivalRequest.getCurrentDirection());
					elevator.setCurrentFloor(arrivalRequest.getFloorArrivedAt());
					ElevatorMotorRequest motorRequest = new ElevatorMotorRequest(getSource(), elevator.getDirection());

					if (elevator.getCurrentFloor() == elevator.getCurrentDestinationFloor()) {
						elevator.setCurrentDestinationFloor(-1);
						file.writeToFile(getSource() + "\n Destination floor has been reached\n");
					}
					// Check if anyone needs to be dropped off on this floor
					for (int i = 0; i < requests.size(); i++) {
						if (requests.get(i).isOriginFloorCompleted() == false
								&& requests.get(i).getOrginFloor() == elevator.getCurrentFloor()) {
							motorRequest = new ElevatorMotorRequest(getSource(), ElevatorDirection.IDLE);
						}
						if (requests.get(i).isOriginFloorCompleted() == true
								&& requests.get(i).isDestinationFloorCompleted() == false
								&& requests.get(i).getDestinationFloor() == elevator.getCurrentFloor()) {
							motorRequest = new ElevatorMotorRequest(getSource(), ElevatorDirection.IDLE);
						}
					}

					elevator.setDirection(motorRequest.getRequestedDirection());
					// Send arrival notice to floor
					if (elevator.getDirection() == ElevatorDirection.IDLE) {
						dispatchRequestToFloorSubsystem(new ElevatorArrivalRequest(elevator.getSource(),
								elevator.getCurrentFloor(), elevator.getDirection()),
								floors.get(elevator.getCurrentFloor()));
					}
					dispatchRequestToElevatorSubsystem(motorRequest, elevator);
				}
				// Tell elevator to open or close doors, if closing go to next destination or
				// continue ongoing
				if (request instanceof ElevatorDoorRequest) {
					ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;

					if (doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
						// If the current floor request isnt completed, go towards it (destination)
						elevator.setDoorStatus(ElevatorDoorStatus.CLOSED);
						if (elevator.getCurrentDestinationFloor() != -1) {
							ElevatorDirection elevatorDirection = getDirectionFromFloor(
									elevator.getCurrentDestinationFloor(), elevator);
							if (ElevatorDirection.IDLE == elevatorDirection) {
								file.writeToFile(elevator.getRequests().toString());
							} else {
								dispatchRequestToElevatorSubsystem(
										new ElevatorMotorRequest(getSource(), elevatorDirection), elevator);
							}
						} else {
							if (elevator.getRequests().size() > 0) {
								PersonRequest closestRequest = elevator.closestRequest();
								int destination = closestRequest.isOriginFloorCompleted()
										? closestRequest.getDestinationFloor()
										: closestRequest.getOrginFloor();
								dispatchRequestToElevatorSubsystem(new ElevatorDestinationRequest(getSource(),
										destination, getDirectionFromFloor(destination, elevator)), elevator);
							}
						}
					} else {
						elevator.setDoorStatus(ElevatorDoorStatus.OPENED);
						dispatchRequestToFloorSubsystem(new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED),
								floors.get(elevator.getCurrentFloor()));
						dispatchRequestToElevatorSubsystem(
								new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED), elevator);
					}
				}
				if (request instanceof ElevatorPassengerWaitRequest) {
					ElevatorPassengerWaitRequest passengerWaitRequest = (ElevatorPassengerWaitRequest) request;
					if (passengerWaitRequest.getState() == WaitState.WAITING) {
						dispatchRequestToElevatorSubsystem(new ElevatorPassengerWaitRequest(getSource(),
								passengerWaitRequest.getWaitTime(), WaitState.WAITING), elevator);
					} else {
						ArrayList<PersonRequest> requests = elevator.getRequests();

						// Clear all requests on floor
						int loopCount = requests.size();
						int numberOfDeletions = 0;
						for (int i = 0; i < loopCount; i++) {
							// Add all the people coming into elevator at this floor (people at floor)
							int actualIndex = i - numberOfDeletions;
							if (requests.get(actualIndex).isOriginFloorCompleted() == false
									&& requests.get(actualIndex).getOrginFloor() == elevator.getCurrentFloor()) {
								elevator.setPersonRequestOriginFloorCompleted(actualIndex, true);
								elevator.addPassenger();
								file.writeToFile("************************\n " + elevator.getSource()
										+ "Added a passenger: \n" + requests.get(actualIndex));
							}
							// Remove all the people coming from the elevator at this floor (people at
							// floor)
							if (requests.get(actualIndex).isOriginFloorCompleted() == true
									&& requests.get(actualIndex).isDestinationFloorCompleted() == false
									&& requests.get(actualIndex).getDestinationFloor() == elevator.getCurrentFloor()) {
								PersonRequest personRequest = elevator.removeFromRequests(actualIndex);
								elevator.subtractPassenger();
								personRequest.setDestinationFloorCompleted(true);
								numberOfDeletions++;
								file.writeToFile("************************\n " + elevator.getSource()
										+ "Completed passengers request: \n" + personRequest);
							}
						}
						dispatchRequestToFloorSubsystem(new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.CLOSED),
								floors.get(elevator.getCurrentFloor()));
						dispatchRequestToElevatorSubsystem(
								new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.CLOSED), elevator);
					}
				}
				if (request instanceof ElevatorEmergencyRequest) {
					ElevatorEmergencyRequest emergencyRequest = (ElevatorEmergencyRequest) request;

					// Not needed for this iter (iter 3)
				}
				if (request instanceof ElevatorFaultRequest) {
					ElevatorFaultRequest faultRequest = (ElevatorFaultRequest) request;
					// Not needed for this iter (iter 3)
				}
			}
		}

		case INVALID_REQUEST -> file.writeToFile(this + " received and discarded an invalid request");

		}

		advanceState(request);
	}

	/**
	 * Checks to see if anyone on this floor needs to use the elevator
	 *
	 * @param elevator The elevator who contains the person request
	 */
	private boolean needToOpenDoors(SchedulerElevatorInfo elevator) {
		boolean needToOpenDoors = false;

		if (elevator.getDirection() == ElevatorDirection.IDLE) {
			for (int i = 0; i < elevator.getRequests().size(); i++) {
				if (elevator.getRequests().get(i).isOriginFloorCompleted() == false
						&& elevator.getRequests().get(i).getOrginFloor() == elevator.getCurrentFloor()) {
					needToOpenDoors = true;
				}
				if (elevator.getRequests().get(i).isOriginFloorCompleted() == true
						&& elevator.getRequests().get(i).isDestinationFloorCompleted() == false
						&& elevator.getRequests().get(i).getDestinationFloor() == elevator.getCurrentFloor()) {
					needToOpenDoors = true;
				}
			}
		}
		return needToOpenDoors;
	}

	/**
	 * Handles a person request by checking if any of its floor requests (origin or
	 * destination) are completed
	 *
	 * @param personRequest A persons request
	 * @param elevator      The elevator who contains the person request
	 */
	private void handlePersonRequest(PersonRequest personRequest, SchedulerElevatorInfo elevator) {
		if (!personRequest.isOriginFloorCompleted()) {
			dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(getSource(),
					getDirectionFromFloor(personRequest.getOrginFloor(), elevator)), elevator);
		} else {
			if (!personRequest.isDestinationFloorCompleted()) {
				dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(getSource(),
						getDirectionFromFloor(personRequest.getDestinationFloor(), elevator)), elevator);
			}
		}
	}

	/**
	 * Gets ElevatorDirection from elevator to floor
	 *
	 * @param destinationFloor The floor that needs to be hit
	 * @param elevator         The elevator to go to this floor
	 */
	private ElevatorDirection getDirectionFromFloor(int destinationFloor, SchedulerElevatorInfo elevator) {
		if (destinationFloor == elevator.getCurrentFloor())
			return ElevatorDirection.IDLE;
		else if (destinationFloor > elevator.getCurrentFloor())
			return ElevatorDirection.UP;
		else
			return ElevatorDirection.DOWN;
	}

	/**
	 * True if elevator is coming towards, False otherwise
	 *
	 * @param elevator         The elevator to go to this floor
	 * @param destinationFloor The floor that needs to be hit
	 */
	private boolean elevatorComingTowardsFloor(SchedulerElevatorInfo elevator, int destinationFloor) {
		if (elevator.getDirection() == ElevatorDirection.IDLE)
			return true;
		if (elevator.getDirection() == ElevatorDirection.UP) {
			if (elevator.getCurrentFloor() <= destinationFloor)
				return true;
		}
		if (elevator.getDirection() == ElevatorDirection.DOWN) {
			if (elevator.getCurrentFloor() >= destinationFloor)
				return true;
		}
		return false;
	}

	/**
	 * Selects elevator best fit for this file request
	 *
	 * @param fileRequest A input from the input file
	 */
	private SchedulerElevatorInfo selectElevator(FileRequest fileRequest) {
		SchedulerElevatorInfo chosenElevator = elevators.get(0);
		boolean elevatorWasPicked = false;

		for (int i = 1; i < elevators.size(); i++) {
			SchedulerElevatorInfo elevator = elevators.get(i);

			// Is this elevator coming towards me
			if (elevatorComingTowardsFloor(elevator, fileRequest.getOriginFloor())) {
				if (Math.abs(fileRequest.getOriginFloor() - elevator.getCurrentFloor()) < Math
						.abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())
						&& elevator.getPassengers() < project.Config.MAX_PASSENGERS_IN_ELEVATOR) {
					// Elevator cannot have more than 5 passengers compared to chosen elevator to
					// become chosen elevator
					if (chosenElevator.getPassengers() - elevator.getPassengers() > -4) {
						chosenElevator = elevator;
						elevatorWasPicked = true;
					}
				}
				// If elevator has 1 less requests (passengers to pick up) make it the chosen
				// elevator
				if (elevator.getNumberOfRequests() + 1 < chosenElevator.getNumberOfRequests()) {
					chosenElevator = elevator;
					elevatorWasPicked = true;
				}
			}
		}

		// If the elevator was never decided then whoever is closest gets picked
		if (!elevatorWasPicked) {
			for (int i = 1; i < elevators.size(); i++) {
				SchedulerElevatorInfo elevator = elevators.get(i);
				if (Math.abs(fileRequest.getOriginFloor() - elevator.getCurrentFloor()) < Math
						.abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())
						&& elevator.getPassengers() < project.Config.MAX_PASSENGERS_IN_ELEVATOR) {
					chosenElevator = elevator;
					elevatorWasPicked = true;
				}
			}
		}

		return chosenElevator;
	}

	/**
	 * Advances this Scheduler's state.
	 *
	 * @param request The Request whose properties will be used to determine this
	 *                Scheduler's next state.
	 */
	private synchronized void advanceState(Request request) {
		state = state.advance(request);
	}

	/**
	 * Get subsystem identification
	 * 
	 * @return this subsystems identification
	 */
	public SubsystemSource getSource() {
		return new SubsystemSource(SubsystemSource.Subsystem.SCHEDULER, "1");
	}

	/**
	 * Returns a String representation of this Scheduler.
	 *
	 * @return a String representation of this Scheduler.
	 */
	@Override
	public String toString() {
		return "Scheduler";
	}

	/**
	 * Drives this Scheduler to fetch and dispatch requests.
	 */
	@Override
	public void run() {
		file.writeToFile("Scheduler operational...\n");
		while (true) {
			consumeRequest(fetchRequest());
		}
	}

	/**
	 * Initializes and starts the Scheduler thread.
	 *
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		Scheduler scheduler = new Scheduler(SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort(),
				SCHEDULER_UDP_INFO.getOutSocketPort());
		Thread schedulerThread = new Thread(scheduler);
		schedulerThread.start();
	}

}