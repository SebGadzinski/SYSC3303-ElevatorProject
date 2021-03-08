package project.systems;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A request/response transmission intermediary for elevator and floor subsystems;
 * schedules and coordinates elevators in optimally servicing passenger requests.
 *
 * @author Paul Roode (iter 3 and 2), Sebastian Gadzinski (iter 1)
 * @version Iteration 3
 */

public class Scheduler extends AbstractSubsystem implements Runnable {

    private final List<ElevatorSubsystem> elevatorSubsystems; // deprecated (needs to be updated/removed)
    private final List<FloorSubsystem> floorSubsystems; // deprecated
    private SchedulerState state;

    // for FIFO (just for iter 2 - throughput will be maximized in the next iter)
    private final List<FileRequest> fileRequestsQueue;
    private boolean isLastFileRequestFulfilled;

    /**
     * A parameterized Scheduler constructor that initializes all fields.
     *
     * @param inetAddress        The Scheduler's IP address.
     * @param inSocketPort       The Scheduler's inlet socket port number.
     * @param outSocketPort      The Scheduler's outlet socket port number.
     * @param elevatorSubsystems The elevator subsystems with which the Scheduler will communicate.
     * @param floorSubsystems    The floor subsystems with which the Scheduler will communicate.
     */
    public Scheduler(InetAddress inetAddress,
                     int inSocketPort,
                     int outSocketPort,
                     List<ElevatorSubsystem> elevatorSubsystems, // deprecated
                     List<FloorSubsystem> floorSubsystems) { // deprecated

        super(inetAddress, inSocketPort, outSocketPort);
        this.elevatorSubsystems = Collections.synchronizedList(elevatorSubsystems);
        this.floorSubsystems = Collections.synchronizedList(floorSubsystems);
        state = SchedulerState.AWAIT_REQUEST;
        fileRequestsQueue = new ArrayList<>();
        isLastFileRequestFulfilled = true;

    }

    /**
     * Dispatches the given Request to the given ElevatorSubsystem.
     *
     * @param request           The Request to be dispatched.
     * @param elevatorSubsystem The ElevatorSubsystem to which the Request will be dispatched.
     */
    public synchronized void dispatchRequestToElevatorSubsystem(Request request, ElevatorSubsystem elevatorSubsystem) {
        request.setSource(Source.SCHEDULER);
        sendRequest(request, elevatorSubsystem);
        System.out.println(this + " says:");
        System.out.println(this + " sent a request to ElevatorSubsystem:");
        System.out.println(request);
        System.out.println();
    }

    /**
     * Dispatches the given Request to the given FloorSubsystem.
     *
     * @param request        The Request to be dispatched.
     * @param floorSubsystem The FloorSubsystem to which the Request will be dispatched.
     */
    public synchronized void dispatchRequestToFloorSubsystem(Request request, FloorSubsystem floorSubsystem) {
        request.setSource(Source.SCHEDULER);
        sendRequest(request, floorSubsystem);
        System.out.println(this + " says:");
        System.out.println(this + " sent a request to FloorSubsystem:");
        System.out.println(request);
        System.out.println();
    }

    /**
     * Fetches a Request, waiting if necessary until one becomes available; then advances this Scheduler's state.
     *
     * @return the fetched Request.
     */
    public synchronized Request fetchRequest() {

        Request request = waitForRequest();

        // printing
        System.out.println(this + " says:");
        System.out.println("Request received by " + this + " from " + request.getSource() + ":");
        System.out.println(request);
        System.out.println();

        advanceState(request);
        return request;

    }

    /**
     * Processes the given Request and issues commands accordingly, then advances this Scheduler's state.
     *
     * @param request The Request to be processed.
     */
    private synchronized void consumeRequest(Request request) {

        switch (state) {

            // command an elevator to wait with its doors open for passengers to board
            case DISPATCH_ELEVATOR_PASSENGER_WAIT_REQUEST_TO_ELEVATOR -> dispatchRequestToElevatorSubsystem(request);

            /*
             dispatch a FileRequest received from a floor to an elevator;
             FIFO is used only in this iter - throughput will be maximized in the next iter
            */
            case DISPATCH_FILE_REQUEST_TO_ELEVATOR -> {
                if (fileRequestsQueue.isEmpty() && isLastFileRequestFulfilled) {
                    dispatchRequestToElevatorSubsystem(request);
                    isLastFileRequestFulfilled = false;
                } else {
                    fileRequestsQueue.add((FileRequest) request);
                }
            }

            case DISPATCH_ELEVATOR_DOOR_REQUEST_TO_ELEVATOR -> {

                // command an elevator to close its doors once it's finished waiting for passengers to board
                if (request instanceof ElevatorPassengerWaitRequest) {
                    dispatchRequestToElevatorSubsystem(new ElevatorDoorRequest(Source.SCHEDULER, ElevatorDoorStatus.CLOSED));
                }

                // command an elevator to open its doors once it's reached its destination and stopped moving
                else if (request instanceof ElevatorDoorRequest) {
                    dispatchRequestToElevatorSubsystem(request);
                }
            }

            case DISPATCH_MOTOR_REQUEST_TO_ELEVATOR -> {

                // command an elevator to move to its next destination
                if (request instanceof ElevatorDestinationRequest) {
                    dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(Source.SCHEDULER, ((ElevatorDestinationRequest) request).getDirection()));
                }

                // command an elevator to stop moving once it's reached its destination
                else if (request instanceof ElevatorArrivalRequest) {
                    System.out.println(this + " received confirmation of elevator arrival:\n" + request);
                    dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(Source.SCHEDULER, ElevatorDirection.IDLE));
                }
            }

            /*
             notify the floor that its FileRequest has been fulfilled;
             dispatch the next FileRequest (if it exists) to an elevator
            */
            case DISPATCH_FILE_REQUEST_TO_FLOOR -> {
                isLastFileRequestFulfilled = true;
                dispatchRequestToFloorSubsystem(request);
                if (!fileRequestsQueue.isEmpty()) {
                    dispatchRequestToElevatorSubsystem(fileRequestsQueue.remove(0));
                    isLastFileRequestFulfilled = false;
                }
            }

            // command an elevator to continue moving if it hasn't yet reached its destination
            case CONSUME_ELEVATOR_ARRIVAL_REQUEST -> dispatchRequestToElevatorSubsystem(
                    new ElevatorMotorRequest(Source.SCHEDULER, ((ElevatorArrivalRequest) request).getCurrentDirection())
            );

            case INVALID_REQUEST -> System.out.println(this + " received and discarded an invalid request");

        }

        advanceState(request);

    }

    /**
     * Advances this Scheduler's state.
     *
     * @param request The Request whose properties will be used to determine this Scheduler's next state.
     */
    private synchronized void advanceState(Request request) {
        state = state.advance(request);
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
        System.out.println("Scheduler operational...\n");
        while (true) {
            consumeRequest(fetchRequest());
        }
    }

}