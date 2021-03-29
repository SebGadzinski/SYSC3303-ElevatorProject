package project.systems;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.*;
import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;
import project.utils.datastructs.ElevatorFaultRequest.ElevatorFault;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.FloorEmergencyRequest.FloorEmergency;
import project.utils.objects.general.CreateFile;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static project.Config.*;

/**
 * A request/response transmission intermediary for elevator and floor subsystems;
 * schedules and coordinates elevators in optimally servicing passenger requests.
 *
 * @author Paul Roode (iter 3 and 2), Sebastian Gadzinski (iter 3 and 1)
 * @version Iteration 3
 */

public class Scheduler extends AbstractSubsystem implements Runnable {

    private SchedulerState state;
    private final List<SchedulerElevatorInfo> elevators;
    private final List<SchedulerFloorInfo> floors;
    private final CreateFile file;

    /**
     * A parameterized Scheduler constructor that initializes all fields, including those inherited.
     *
     * @param inetAddress   The Scheduler's IP address.
     * @param inSocketPort  The Scheduler's inlet socket port number.
     * @param outSocketPort The Scheduler's outlet socket port number.
     */
    public Scheduler(InetAddress inetAddress, int inSocketPort, int outSocketPort) {

        super(inetAddress, inSocketPort, outSocketPort);
        file = new CreateFile("schedulerFile.txt");
        state = SchedulerState.AWAIT_REQUEST;

        elevators = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_ELEVATORS; ++i) {
            elevators.add(new SchedulerElevatorInfo(
                    Integer.toString(i),
                    project.Config.ELEVATORS_UDP_INFO[i],
                    ElevatorDirection.IDLE,
                    ElevatorDoorStatus.CLOSED, 0
            ));
        }

        floors = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_FLOORS; ++i) {
            floors.add(new SchedulerFloorInfo(Integer.toString(i), project.Config.FLOORS_UDP_INFO[i]));
        }

    }

    /**
     * Dispatches the given Request to the given ElevatorSubsystem.
     *
     * @param request      The Request to be dispatched.
     * @param elevatorInfo The UDP info of the ElevatorSubsystem to which the Request will be dispatched.
     */
    public synchronized void dispatchRequestToElevatorSubsystem(Request request, SchedulerElevatorInfo elevatorInfo) {
    	if(request instanceof ElevatorMotorRequest && elevatorInfo.isTimerRunning() == false) {
    		ElevatorMotorRequest tmp = (ElevatorMotorRequest) request;
    		if(tmp.getRequestedDirection() != ElevatorDirection.IDLE) {
    			file.writeToFile("Starting the timer for elevator: " + elevatorInfo.getId());
    			//System.out.println("Starting the timer for elevator: " + elevatorInfo.getId());
    			elevatorInfo.startTimer();
    			try {
    				Thread.sleep(100);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			//System.out.println(elevatorInfo.getTimerRunning());	
    		}
    	}

        request.setSource(elevatorInfo.getSource());
        sendRequest(request, elevatorInfo.getUdpInfo().getInetAddress(), elevatorInfo.getUdpInfo().getInSocketPort());
        file.writeToFile(this + " says:");
        file.writeToFile(this + " sent a request to " + elevatorInfo);
        file.writeToFile(request.toString());
    }

    /**
     * Dispatches the given Request to the given FloorSubsystem.
     *
     * @param request   The Request to be dispatched.
     * @param floorInfo The UDP info of the FloorSubsystem to which the Request will be dispatched.
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
                if (request instanceof FileRequest) {
                    FileRequest fileRequest = (FileRequest) request;
                	SchedulerElevatorInfo elevator = selectElevator(fileRequest);
                    handleFileRequest(fileRequest, elevator);
                }
                if (request.getSource().getSubsystem() == SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM) {
                    SchedulerElevatorInfo elevator = elevators.get(Integer.parseInt(request.getSource().getId()));

                    if(elevator.getTimeOut()) {
                    	file.writeToFile("Shutting down elevator: " + elevator.getId());             
                    	this.dispatchRequestToElevatorSubsystem(new ElevatorEmergencyRequest(getSource(), ElevatorEmergency.SHUTDOWN, ElevatorEmergencyRequest.INCOMPLETE_EMERGENCY, null, null),
                                elevator);                   
                    }
                    // If anybody has a request on this floor open the doors, otherwise go toward destination
                    else if (request instanceof ElevatorDestinationRequest) {
                    	handleElevatorDestinationRequest((ElevatorDestinationRequest) request, elevator);
                    }
                    // If anybody has a request on this floor open the doors, otherwise go towards destination
                    else if (request instanceof ElevatorArrivalRequest) {
                    	handleElevatorArrivalRequest((ElevatorArrivalRequest) request, elevator);
                    }
                    // Tell elevator to open or close doors, if closing go to next destination or continue ongoing
                    else if (request instanceof ElevatorDoorRequest) {
                        handleElevatorDoorRequest((ElevatorDoorRequest) request, elevator);
                    }
                    else if (request instanceof ElevatorPassengerWaitRequest) {
                    	handleElevatorPassengerWaitRequest((ElevatorPassengerWaitRequest) request, elevator);
                    }
                    else if (request instanceof ElevatorFaultRequest) {
                    	handleElevatorFaultRequest((ElevatorFaultRequest) request, elevator);
                    }
                    else if (request instanceof ElevatorEmergencyRequest) {
                    	handleElevatorEmergencyRequest((ElevatorEmergencyRequest) request, elevator);
                    }
                }
            }
            case INVALID_REQUEST -> file.writeToFile(this + " received and discarded an invalid request");
        }
        advanceState(request);
    }
    
    /**
     * Handles FileRequest's from FloorSubsystem
     *
     * @param fileRequest: The FileRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleFileRequest(FileRequest fileRequest, SchedulerElevatorInfo elevator) {
        PersonRequest personRequest = new PersonRequest(
                fileRequest.getOriginFloor(),
                fileRequest.getDestinationFloor(),
                false,
                false
        );
        elevator.addToRequests(personRequest);
        file.writeToFile("Added Request to list of requests to: \n" + elevator.getSource());

        if (elevator.getCurrentDestinationFloor() == -1) {
            elevator.setCurrentDestinationFloor(fileRequest.getOriginFloor());
            dispatchRequestToElevatorSubsystem(fileRequest, elevator);
        }
    }
    
    /**
     * Handles ElevatorDestinationRequest's come from ElevatorSubsytem after they have been given a FileRequest
     * They are basically confirmation requests for the FileRequests sent
     *
     * @param destinationRequest: The ElevatorDestinationRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorDestinationRequest(ElevatorDestinationRequest destinationRequest, SchedulerElevatorInfo elevator) {
    	//Is elevator currently going somewhere
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
        	//If there are any person requests, see which is the closest and process it
             if (elevator.getRequests().size() > 0) {
                 PersonRequest closestRequest = elevator.closestRequest();
                 elevator.setCurrentDestinationFloor(
                         closestRequest.isOriginFloorCompleted() ? closestRequest.getDestinationFloor()
                                 : closestRequest.getOriginFloor());
                 handlePersonRequest(closestRequest, elevator);
             } else {
                 elevator.setCurrentDestinationFloor(-1);
             }
         }
    }
    
    /**
     * Handles ElevatorArrivalRequest's from ElevatorSubsytem
     *
     * @param arrivalRequest: The ElevatorArrivalRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorArrivalRequest(ElevatorArrivalRequest arrivalRequest, SchedulerElevatorInfo elevator) {
    	//Stop the timer that is checking if the elevator kept moving or wont stop
    	elevator.stopTimer();
    	file.writeToFile("Stopping the timer for elevator: " + elevator.getId());
 		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        ArrayList<PersonRequest> requests = elevator.getRequests();

        elevator.setDirection(arrivalRequest.getCurrentDirection());
        elevator.setCurrentFloor(arrivalRequest.getFloorArrivedAt());
        ElevatorMotorRequest motorRequest = new ElevatorMotorRequest(getSource(), elevator.getDirection());

        if (elevator.getCurrentFloor() == elevator.getCurrentDestinationFloor()) {
            elevator.setCurrentDestinationFloor(-1);
            file.writeToFile(getSource() + "\n Destination floor has been reached\n");
        }
        // Check if anyone needs to be dropped off on this floor
        for (PersonRequest personRequest : requests) {
            if (!personRequest.isOriginFloorCompleted()
                    && personRequest.getOriginFloor() == elevator.getCurrentFloor()) {
                motorRequest = new ElevatorMotorRequest(getSource(), ElevatorDirection.IDLE);
            }
            if (personRequest.isOriginFloorCompleted()
                    && !personRequest.isDestinationFloorCompleted()
                    && personRequest.getDestinationFloor() == elevator.getCurrentFloor()) {
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
    
    /**
     * Handles ElevatorDoorRequest from ElevatorSubsytem
     *
     * @param doorRequest: The ElevatorDoorRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorDoorRequest(ElevatorDoorRequest doorRequest, SchedulerElevatorInfo elevator) {
        if (doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
            // If the current floor request isn't completed, go towards it (destination)
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
                            : closestRequest.getOriginFloor();
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
    
    /**
     * Handles ElevatorPassengerWaitRequest from ElevatorSubsytem
     *
     * @param passengerWaitRequest: The ElevatorPassengerWaitRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorPassengerWaitRequest(ElevatorPassengerWaitRequest passengerWaitRequest, SchedulerElevatorInfo elevator) {
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
                if (!requests.get(actualIndex).isOriginFloorCompleted()
                        && requests.get(actualIndex).getOriginFloor() == elevator.getCurrentFloor()) {
                    elevator.setPersonRequestOriginFloorCompleted(actualIndex, true);
                    elevator.addPassenger();
                    file.writeToFile("************************\n " + elevator.getSource()
                            + "Added a passenger: \n" + requests.get(actualIndex));
                }
                // Remove all the people coming from the elevator at this floor (people at
                // floor)
                if (requests.get(actualIndex).isOriginFloorCompleted()
                        && !requests.get(actualIndex).isDestinationFloorCompleted()
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
    
    /**
     * Handles ElevatorFaultRequest from ElevatorSubsytem
     *
     * @param faultRequest: The ElevatorFaultRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorFaultRequest(ElevatorFaultRequest faultRequest, SchedulerElevatorInfo elevator) {    	
    	dispatchRequestToElevatorSubsystem(new ElevatorEmergencyRequest(getSource(), ElevatorEmergency.FIX, ElevatorEmergencyRequest.INCOMPLETE_EMERGENCY, null, null),
                elevator);
    }
    
    /**
     * Handles ElevatorEmergencyRequest from ElevatorSubsytem
     *
     * @param emergencyRequest: The ElevatorEmergencyRequest to be processed.
     * @param elevator: The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorEmergencyRequest(ElevatorEmergencyRequest emergencyRequest, SchedulerElevatorInfo elevator) {    	
    	if(ElevatorEmergencyRequest.COMPLETED_EMERGENCY == emergencyRequest.getStatus() && emergencyRequest.getEmergencyState() == ElevatorEmergency.FIX) {
    		// Check if anyone needs to be dropped off on this floor
    		elevator.setDirection(emergencyRequest.getDirectionState());
    		elevator.setDoorStatus(emergencyRequest.getDoorState());
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
    	}
    	//If it was a SHUTDOWN, remove the elevators from the operational elevators list
    	else {
    		elevators.remove(elevator);
        	if(elevators.size() == 0) {
        		for(int i = 0; i < this.floors.size(); i++) {
        			this.dispatchRequestToFloorSubsystem(new FloorEmergencyRequest(getSource(), FloorEmergency.SHUTDOWN), floors.get(i));
        		}
        		System.exit(1);
        	}
    	}
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
                if (!elevator.getRequests().get(i).isOriginFloorCompleted()
                        && elevator.getRequests().get(i).getOriginFloor() == elevator.getCurrentFloor()) {
                    needToOpenDoors = true;
                }
                if (elevator.getRequests().get(i).isOriginFloorCompleted()
                        && !elevator.getRequests().get(i).isDestinationFloorCompleted()
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
                    getDirectionFromFloor(personRequest.getOriginFloor(), elevator)), elevator);
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
            return elevator.getCurrentFloor() >= destinationFloor;
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
                        .abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())) {
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
                        .abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())) {
                    chosenElevator = elevator;
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
        Scheduler scheduler = new Scheduler(
                SCHEDULER_UDP_INFO.getInetAddress(),
                SCHEDULER_UDP_INFO.getInSocketPort(),
                SCHEDULER_UDP_INFO.getOutSocketPort()
        );
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();
    }

}