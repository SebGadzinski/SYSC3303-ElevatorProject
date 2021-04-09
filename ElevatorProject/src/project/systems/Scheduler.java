package project.systems;

import project.Config;
import project.gui.ElevatorProjectGUI;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.*;
import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.FloorEmergencyRequest.FloorEmergency;
import project.utils.datastructs.SubsystemSource.Subsystem;
import project.utils.objects.general.CreateFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import static project.Config.*;

/**
 * A request/response transmission intermediary for elevator and floor subsystems;
 * schedules and coordinates elevators in optimally servicing passenger requests.
 *
 * @author Sebastian Gadzinski
 * @author Paul Roode
 * @version Iteration 5
 */

public class Scheduler extends AbstractSubsystem {

    private SchedulerState state;
    private final List<SchedulerElevatorInfo> elevators;
    private final List<SchedulerFloorInfo> floors;
    private final CreateFile file;
    private ElevatorProjectGUI projectGUI;
    private final ArrayList<LinkedHashMap<Integer, Integer>> destinationRequests = new ArrayList<>();

    public Scheduler(UDPInfo schedulerUDPInfo, UDPInfo[] elevatorsUDPInfo, UDPInfo[] floorsUDPInfo) {

        super(schedulerUDPInfo);
        file = new CreateFile("schedulerFile.txt");
        state = SchedulerState.AWAIT_REQUEST;

        elevators = new ArrayList<>();
        for (int i = 0; i < elevatorsUDPInfo.length; ++i) {
            elevators.add(
                    new SchedulerElevatorInfo(
                            Integer.toString(i),
                            elevatorsUDPInfo[i],
                            ElevatorDirection.IDLE,
                            ElevatorDoorStatus.CLOSED,
                            0
                    )
            );
        }

        floors = new ArrayList<>();
        for (int i = 0; i < floorsUDPInfo.length; ++i) {
            floors.add(
                    new SchedulerFloorInfo(
                            Integer.toString(i),
                            floorsUDPInfo[i]
                    )
            );
            destinationRequests.add(new LinkedHashMap<>());
        }

    }

    /**
     * Dispatches the given Request to the given ElevatorSubsystem.
     *
     * @param request      The Request to be dispatched.
     * @param elevatorInfo The UDP info of the ElevatorSubsystem to which the Request will be dispatched.
     */
    public synchronized void dispatchRequestToElevatorSubsystem(Request request, SchedulerElevatorInfo elevatorInfo) {
        if (request instanceof ElevatorMotorRequest && !elevatorInfo.isTimerRunning()) {
            ElevatorMotorRequest tmp = (ElevatorMotorRequest) request;
            if (tmp.getRequestedDirection() != ElevatorDirection.IDLE) {
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
        printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevatorInfo);
        printToFileFromElevator(this + " sent a request to " + elevatorInfo, elevatorInfo);
        printToFileFromElevator(request.toString(), elevatorInfo);
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
        boolean printable = !Config.FAULT_PRINTING;
        if (request.getSource().getSubsystem() == Subsystem.ELEVATOR_SUBSYSTEM) {
            if (elevators.get(Integer.parseInt(request.getSource().getId())).isPrintingEnabled()) {
                printable = true;
            }
        }

        if (printable) {
            file.writeToFile("\nTimeStamp: " + getTimestamp());
            file.writeToFile(this + " sent a request to " + floorInfo);
            file.writeToFile(request.toString());
        }
    }

    /**
     * Fetches a Request, waiting if necessary until one becomes available; then
     * advances this Scheduler's state.
     *
     * @return the fetched Request.
     */
    public synchronized Request fetchRequest() {

        Request request = waitForRequest();
        boolean printable = !Config.FAULT_PRINTING;
        if (request.getSource().getSubsystem() == Subsystem.ELEVATOR_SUBSYSTEM) {
            if (elevators.get(Integer.parseInt(request.getSource().getId())).isPrintingEnabled()) {
                printable = true;
            }
        }

        if (printable) {
            file.writeToFile("\nTimeStamp: " + getTimestamp());
            file.writeToFile("Request received by " + this + " from " + request.getSource());
            file.writeToFile(request.toString());
        }

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
                    // Update GUI
                    projectGUI.updateElevator(elevator);
                }
                if (request.getSource().getSubsystem() == SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM) {
                    SchedulerElevatorInfo elevator = elevators.get(Integer.parseInt(request.getSource().getId()));

                    if (elevator.getTimeOut()) {
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
                    } else if (request instanceof ElevatorPassengerWaitRequest) {
                        handleElevatorPassengerWaitRequest((ElevatorPassengerWaitRequest) request, elevator);
                    } else if (request instanceof ElevatorFaultRequest) {
                        handleElevatorFaultRequest((ElevatorFaultRequest) request, elevator);
                    } else if (request instanceof ElevatorEmergencyRequest) {
                        handleElevatorEmergencyRequest((ElevatorEmergencyRequest) request, elevator);
                    }
                    projectGUI.updateElevator(elevator);
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
     * @param elevator:    The SchedulerElevatorInfo to be manipulated
     */
    private synchronized void handleFileRequest(FileRequest fileRequest, SchedulerElevatorInfo elevator) {
        PersonRequest personRequest = new PersonRequest(
                fileRequest.getOriginFloor(),
                fileRequest.getDestinationFloor(),
                false,
                false,
                fileRequest.getFault()
        );

        //Adding person to floor : GUI
        projectGUI.addRequestToFloor(personRequest.hashCode(), fileRequest.getOriginFloor(), personRequest.getDestinationFloor());

        if (fileRequest.getFault() > 0) {
            elevator.setPrintingEnabled(true);
            printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevator);
            printToFileFromElevator("Elevator Number: " + elevator.getId(), elevator);
            printToFileFromElevator("Fault Detected With Person " + personRequest, elevator);
            if (Config.FAULT_PRINTING)
                elevator.setPrintingEnabled(false);
        }

        elevator.addToRequests(personRequest);
        printToFileFromElevator("Added Request to list of requests to: \n" + elevator.getSource(), elevator);

        if (elevator.getCurrentDestinationFloor() == -1) {
            elevator.setCurrentDestinationFloor(fileRequest.getOriginFloor());
            dispatchRequestToElevatorSubsystem(fileRequest, elevator);
        }
    }

    /**
     * Handles ElevatorDestinationRequest's come from ElevatorSubsystems after they have been given a FileRequest
     * They are basically confirmation requests for the FileRequests sent
     *
     * @param destinationRequest: The ElevatorDestinationRequest to be processed.
     * @param elevator:           The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorDestinationRequest(ElevatorDestinationRequest destinationRequest, SchedulerElevatorInfo elevator) {
        //Is elevator currently going somewhere
        if (elevator.getCurrentDestinationFloor() != -1) {
            ElevatorDirection direction = getDirectionFromFloor(elevator.getCurrentDestinationFloor(),
                    elevator.getCurrentFloor());
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
                                getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor())),
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
     * Handles ElevatorArrivalRequest's from ElevatorSubsystems
     *
     * @param arrivalRequest: The ElevatorArrivalRequest to be processed.
     * @param elevator:       The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorArrivalRequest(ElevatorArrivalRequest arrivalRequest, SchedulerElevatorInfo elevator) {
        elevator.setLamp(arrivalRequest.getFloorArrivedAt(), false);

        //Stop the timer that is checking if the elevator kept moving or wont stop
        elevator.setPrintingEnabled(true);
        elevator.stopTimer();
        printToFileFromElevator("Stopping the timer for elevator: " + elevator.getId(), elevator);
        if (Config.FAULT_PRINTING) elevator.setPrintingEnabled(false);
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
        }

        boolean needToStop = false;

        // Check if anyone needs to be dropped off on this floor
        for (PersonRequest personRequest : requests) {
            if (!personRequest.isOriginFloorCompleted()
                    && personRequest.getOriginFloor() == elevator.getCurrentFloor()) {
                needToStop = true;
                break;
            }
            if (personRequest.isOriginFloorCompleted()
                    && !personRequest.isDestinationFloorCompleted()
                    && personRequest.getDestinationFloor() == elevator.getCurrentFloor()) {
                needToStop = true;
                break;
            }
        }

        if (needToStop) {
            motorRequest = new ElevatorMotorRequest(getSource(), ElevatorDirection.IDLE);
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
     * Handles ElevatorDoorRequest from ElevatorSubsystems
     *
     * @param doorRequest: The ElevatorDoorRequest to be processed.
     * @param elevator:    The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorDoorRequest(ElevatorDoorRequest doorRequest, SchedulerElevatorInfo elevator) {
        if (doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
            // If the current floor request isn't completed, go towards it (destination)
            elevator.setDoorStatus(ElevatorDoorStatus.CLOSED);
            if (elevator.getCurrentDestinationFloor() != -1) {
                ElevatorDirection elevatorDirection = getDirectionFromFloor(
                        elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor());
                if (ElevatorDirection.IDLE == elevatorDirection) {
                    printToFileFromElevator(elevator.getRequests().toString(), elevator);
                } else {
                    dispatchRequestToElevatorSubsystem(
                            new ElevatorMotorRequest(getSource(), elevatorDirection), elevator);
                }
            } else {
                if (elevator.getRequests().size() > 0) {
                    PersonRequest closestRequest = elevator.closestRequest();
                    if (!closestRequest.isFaultCompleted() && closestRequest.getFault() > 0 && Config.FAULT_PRINTING)
                        elevator.setPrintingEnabled(false);

                    dispatchRequestToElevatorSubsystem(new ElevatorDestinationRequest(getSource(),
                            elevator.getCurrentDestinationFloor(), getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor())), elevator);
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
     * Handles ElevatorPassengerWaitRequest from ElevatorSubsystems
     *
     * @param passengerWaitRequest: The ElevatorPassengerWaitRequest to be processed.
     * @param elevator:             The SchedulerElevatorInfo to be manipulated
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
            PersonRequest personWithFault = null;
            for (int i = 0; i < loopCount; i++) {
                // Add all the people coming into elevator at this floor (people at floor)
                int actualIndex = i - numberOfDeletions;
                if (!requests.get(actualIndex).isOriginFloorCompleted()
                        && requests.get(actualIndex).getOriginFloor() == elevator.getCurrentFloor()) {
                    if (elevator.getCurrentDestinationFloor() != -1
                            && getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor()) != ElevatorDirection.IDLE
                            && getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor()) !=
                            getDirectionFromFloor(requests.get(actualIndex).getDestinationFloor(), requests.get(actualIndex).getOriginFloor())) {
                        continue;
                    }
                    // If it reached its destination floor, assign this request to be the destination
                    if (elevator.getCurrentDestinationFloor() == -1) {
                        elevator.setCurrentDestinationFloor(requests.get(actualIndex).getDestinationFloor());
                    }
                    // If there is a fault, print it out
                    if (requests.get(actualIndex).getFault() > 0 && !requests.get(actualIndex).isFaultCompleted()) {
                        personWithFault = requests.get(actualIndex);
                        elevator.setPrintingEnabled(true);
                        printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevator);
                        printToFileFromElevator("Elevator Number: " + elevator.getId(), elevator);
                        printToFileFromElevator("Processing Fault, Person " + personWithFault, elevator);
                    }
                    elevator.setPersonRequestOriginFloorCompleted(actualIndex, true);
                    elevator.addPassenger();
                    printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevator);
                    printToFileFromElevator("************************\n" + elevator.getSource()
                            + "\nAdded a passenger: \n" + requests.get(actualIndex), elevator);
                    //Removing person to floor : GUI
                    projectGUI.removeRequestFromFloor(requests.get(actualIndex).hashCode(), requests.get(actualIndex).getOriginFloor());
                    elevator.setLamp(requests.get(actualIndex).getDestinationFloor(), true);
                }
                // Remove all the people coming from the elevator at this floor (people at floor)
                if (requests.get(actualIndex).isOriginFloorCompleted()
                        && !requests.get(actualIndex).isDestinationFloorCompleted()
                        && requests.get(actualIndex).getDestinationFloor() == elevator.getCurrentFloor()) {
                    PersonRequest personRequest = elevator.removeFromRequests(actualIndex);
                    elevator.subtractPassenger();
                    personRequest.setDestinationFloorCompleted(true);
                    if (personRequest.getFault() > 0 && personRequest.isFaultCompleted()) {
                        elevator.setPrintingEnabled(true);
                        printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevator);
                        printToFileFromElevator("Elevator Number: " + elevator.getId(), elevator);
                        printToFileFromElevator("Finished Processing Fault, Person " + personRequest, elevator);
                    }
                    numberOfDeletions++;
                    printToFileFromElevator("\nTimeStamp: " + getTimestamp(), elevator);
                    printToFileFromElevator("************************\n" + elevator.getSource()
                            + "\nCompleted passengers request: \n" + personRequest, elevator);
                    if (personRequest.getFault() > 0 && personRequest.isFaultCompleted())
                        elevator.setPrintingEnabled(false);
                }
            }
            dispatchRequestToFloorSubsystem(new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.CLOSED),
                    floors.get(elevator.getCurrentFloor()));
            if (personWithFault != null) {
                ElevatorDoorRequest door = new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.CLOSED, personWithFault.getFault());
                dispatchRequestToElevatorSubsystem(
                        door, elevator);
                personWithFault.setFaultCompleted(true);
            } else {
                dispatchRequestToElevatorSubsystem(
                        new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.CLOSED), elevator);
            }
        }
    }

    /**
     * Handles ElevatorFaultRequest from ElevatorSubsystems
     *
     * @param faultRequest: The ElevatorFaultRequest to be processed.
     * @param elevator:     The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorFaultRequest(ElevatorFaultRequest faultRequest, SchedulerElevatorInfo elevator) {
        elevator.setRepairing(true);
        dispatchRequestToElevatorSubsystem(new ElevatorEmergencyRequest(getSource(), ElevatorEmergency.FIX, ElevatorEmergencyRequest.INCOMPLETE_EMERGENCY, null, null),
                elevator);
    }

    /**
     * Handles ElevatorEmergencyRequest from ElevatorSubsystems
     *
     * @param emergencyRequest: The ElevatorEmergencyRequest to be processed.
     * @param elevator:         The SchedulerElevatorInfo to be manipulated
     */
    private void handleElevatorEmergencyRequest(ElevatorEmergencyRequest emergencyRequest, SchedulerElevatorInfo elevator) {
        if (ElevatorEmergencyRequest.COMPLETED_EMERGENCY == emergencyRequest.getStatus() && emergencyRequest.getEmergencyState() == ElevatorEmergency.FIX) {
            // Check if anyone needs to be dropped off on this floor
            elevator.setRepairing(false);
            elevator.setDirection(emergencyRequest.getDirectionState());
            elevator.setDoorStatus(emergencyRequest.getDoorState());
            if (needToOpenDoors(elevator)) {
                dispatchRequestToFloorSubsystem(
                        new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED),
                        floors.get(elevator.getCurrentFloor()));
                dispatchRequestToElevatorSubsystem(
                        new ElevatorDoorRequest(getSource(), ElevatorDoorStatus.OPENED), elevator);
            } else {
                if (elevator.getCurrentDestinationFloor() == -1) {
                    PersonRequest closestRequest = elevator.closestRequest();
                    if (!closestRequest.isFaultCompleted() && closestRequest.getFault() > 0 && Config.FAULT_PRINTING)
                        elevator.setPrintingEnabled(false);

                    int destination = closestRequest.isOriginFloorCompleted()
                            ? closestRequest.getDestinationFloor()
                            : closestRequest.getOriginFloor();
                    dispatchRequestToElevatorSubsystem(new ElevatorDestinationRequest(getSource(),
                            destination, getDirectionFromFloor(destination, elevator.getCurrentFloor())), elevator);
                } else {
                    dispatchRequestToElevatorSubsystem(
                            new ElevatorMotorRequest(getSource(),
                                    getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor())),
                            elevator);
                }
            }
        }
        // If it was a SHUTDOWN, remove the elevators from the operational elevators list
        else {
            elevator.setShutDown(true);
            elevators.remove(elevator);
            if (elevators.size() == 0) {
                for (SchedulerFloorInfo floor : this.floors) {
                    this.dispatchRequestToFloorSubsystem(new FloorEmergencyRequest(getSource(), FloorEmergency.SHUTDOWN), floor);
                }
                System.exit(1);
            }
        }
        if (Config.FAULT_PRINTING)
            elevator.setPrintingEnabled(false);
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
                    getDirectionFromFloor(personRequest.getOriginFloor(), elevator.getCurrentFloor())), elevator);
        } else {
            if (!personRequest.isDestinationFloorCompleted()) {
                dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(getSource(),
                        getDirectionFromFloor(personRequest.getDestinationFloor(), elevator.getCurrentFloor())), elevator);
            }
        }
    }

    /**
     * Gets ElevatorDirection from elevator to floor
     *
     * @param destinationFloor The floor that needs to be hit
     * @param originFloor      The floor it is coming from
     */
    private ElevatorDirection getDirectionFromFloor(int destinationFloor, int originFloor) {
        if (destinationFloor == originFloor)
            return ElevatorDirection.IDLE;
        else if (destinationFloor > originFloor)
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
        //Elevator not in use or at floor, allow it
        if (elevator.getCurrentDestinationFloor() == -1 || elevator.getCurrentFloor() == destinationFloor) return true;
        //Elevator is coming towards me from above and the destination of it is my floor or higher, allow it
        if (elevator.getCurrentFloor() >= destinationFloor && elevator.getCurrentDestinationFloor() <= destinationFloor)
            return true;
        //Elevator is coming towards me from below and the destination of it is my floor or less, allow it
        return elevator.getCurrentFloor() <= destinationFloor && elevator.getCurrentDestinationFloor() >= destinationFloor;
    }

    /**
     * Selects elevator best fit for this file request
     *
     * @param fileRequest A input from the input file
     */
    private SchedulerElevatorInfo selectElevator(FileRequest fileRequest) {
        SchedulerElevatorInfo chosenElevator = elevators.get(0);
        boolean elevatorWasPicked = (chosenElevator.getNumberOfRequests() == 0);

        // Find the closest not in use elevator if there are any
        for (int i = 1; i < elevators.size(); i++) {
            SchedulerElevatorInfo elevator = elevators.get(i);
            if (elevator.getNumberOfRequests() == 0 && !elevatorWasPicked) {
                elevatorWasPicked = true;
                chosenElevator = elevator;
            }
            if (elevator.getNumberOfRequests() == 0) {
                if (Math.abs(fileRequest.getOriginFloor() - elevator.getCurrentFloor()) < Math
                        .abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())) {
                    elevatorWasPicked = true;
                    chosenElevator = elevator;
                }
            }
        }
        if (elevatorWasPicked) return chosenElevator;

        // ** All elevators are in use, Selecting best fit **

        for (int i = 1; i < elevators.size(); i++) {
            SchedulerElevatorInfo elevator = elevators.get(i);

            // Is this elevator coming towards me && going in the direction I want
            if (elevatorComingTowardsFloor(elevator, fileRequest.getOriginFloor()) && getDirectionFromFloor(elevator.getCurrentDestinationFloor(), elevator.getCurrentFloor()) ==
                    getDirectionFromFloor(fileRequest.getDestinationFloor(), fileRequest.getOriginFloor())) {

                if (Math.abs(fileRequest.getOriginFloor() - elevator.getCurrentFloor()) < Math
                        .abs(fileRequest.getOriginFloor() - chosenElevator.getCurrentFloor())) {
                    // Elevator cannot have more than 1 passengers compared to chosen elevator to
                    // become chosen elevator
                    if (chosenElevator.getPassengers() - elevator.getPassengers() > -1) {
                        chosenElevator = elevator;
                        elevatorWasPicked = true;
                    }
                }
                // If elevator has 1 less requests (passengers to pick up) make it the chosen elevator
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

    private void printToFileFromElevator(String message, SchedulerElevatorInfo elevator) {
        if (elevator.isPrintingEnabled())
            file.writeToFile(message);
    }

    /**
     * Drives this Scheduler to fetch and dispatch requests.
     */
    @Override
    public void run() {
        file.writeToFile("Scheduler operational...\n");
        projectGUI = new ElevatorProjectGUI();
        Thread GUIThread = new Thread(projectGUI);
        GUIThread.start();
        file.writeToFile("GUI operational...\n");
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
        Scheduler scheduler = new Scheduler(SCHEDULER_UDP_INFO, ELEVATORS_UDP_INFO, FLOORS_UDP_INFO);
        Thread schedulerThread = new Thread(scheduler);
        schedulerThread.start();
    }

}