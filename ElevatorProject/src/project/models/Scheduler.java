package project.models;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * A request/response transmission intermediary between floor and elevator subsystems;
 * schedules and coordinates elevators in servicing passenger requests.
 *
 * @author Paul Roode (iter 2), Sebastian Gadzinski (iter 1)
 * @version Iteration 2
 */

public class Scheduler implements Runnable {

    private final BlockingQueue<Request> requestsFromSubsystems;
    private final BlockingQueue<Request> requestsToElevatorSubsystem;
    private final BlockingQueue<Request> requestsToFloorSubsystem;
    private SchedulerState state;

    // for FIFO (just for iter 2 - throughput will be maximized in the next iter)
    private final ArrayList<FileRequest> fileRequestsQueue;
    private boolean isLastFileRequestFulfilled;

    /**
     * A parameterized Scheduler constructor.
     *
     * @param requestsFromSubsystems      The inlet requests queue.
     * @param requestsToElevatorSubsystem An outlet requests queue to the ElevatorSubsystem.
     * @param requestsToFloorSubsystem    An outlet requests queue to the FloorSubsystem.
     */
    public Scheduler(BlockingQueue<Request> requestsFromSubsystems,
                     BlockingQueue<Request> requestsToElevatorSubsystem,
                     BlockingQueue<Request> requestsToFloorSubsystem) {

        this.requestsFromSubsystems = requestsFromSubsystems;
        this.requestsToElevatorSubsystem = requestsToElevatorSubsystem;
        this.requestsToFloorSubsystem = requestsToFloorSubsystem;
        state = SchedulerState.AWAIT_REQUEST;
        fileRequestsQueue = new ArrayList<>();
        isLastFileRequestFulfilled = true;

    }

    /**
     * Inserts the given request into the outlet requests queue to the FloorSubsystem,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outlet requests queue to the FloorSubsystem.
     */
    public synchronized void dispatchRequestToFloorSubsystem(Request request) {
        request.setSource(Source.SCHEDULER);
        try {
            requestsToFloorSubsystem.put(request);
            System.out.println(this + " sent a request to FloorSubsystem");
            System.out.println(request);
            System.out.println();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Inserts the given request into the outlet requests queue to the ElevatorSubsystem,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outlet requests queue to the ElevatorSubsystem.
     */
    public synchronized void dispatchRequestToElevatorSubsystem(Request request) {
        request.setSource(Source.SCHEDULER);
        try {
            requestsToElevatorSubsystem.put(request);
            System.out.println(this + " sent a request to ElevatorSubsystem:");
            System.out.println(request);
            System.out.println();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    /**
     * Retrieves and removes the head of the inlet requests queue, waiting if necessary
     * until a request becomes available; then advances this Scheduler's state.
     *
     * @return the fetched request.
     */
    public synchronized Request fetchRequest() {
        Request request = null;
        try {
            request = requestsFromSubsystems.take();
            System.out.println("Request received by " + this + " from " + request.getSource() + ":");
            System.out.println(request);
            System.out.println();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        advanceState(request);
        return request;
    }

    /**
     * Consumes/dispatches the given request, then advances this Scheduler's state.
     *
     * @param request The request to be consumed/dispatched.
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
                    System.out.println(this + " received confirmation of elevator arrival:\n" + request + "\n");
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

            case INVALID_REQUEST -> System.out.println(this + " received and discarded an invalid request");

        }

        advanceState(request);

    }

    /**
     * Gets this Scheduler's current state.
     *
     * @return this Scheduler's current state.
     */
    public SchedulerState getState() {
        return state;
    }

    /**
     * Advances this Scheduler's state.
     *
     * @param request The request whose properties will be used to determine this Scheduler's next state.
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