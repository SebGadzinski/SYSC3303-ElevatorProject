package project.models;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;

import java.util.concurrent.BlockingQueue;

/**
 * A request/response transmission intermediary between floor and elevator subsystems.
 *
 * @author Paul Roode (iter 2), Sebastian Gadzinski (iter 1)
 * @version Iteration 2
 */

public class Scheduler implements Runnable {

    private BlockingQueue<Request> requestsFromSubsystems;
    private BlockingQueue<Request> requestsToElevatorSubsystem;
    private BlockingQueue<Request> requestsToFloorSubsystem;
    private SchedulerState state;

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
        this.state = SchedulerState.AWAIT_REQUEST;
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
            System.out.println(this + " sent a request to FloorSubsystem\n");
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
            System.out.println(this + " sent a request to ElevatorSubsystem\n");
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
            if (request instanceof FileRequest) {
                FileRequest fileRequest = (FileRequest) request;
                System.out.println("The request was fulfilled at " + fileRequest.getTime());
                System.out.println("The elevator picked up passengers on floor " + fileRequest.getOriginFloor());
                System.out.println("The elevator arrived at floor " + fileRequest.getDestinationFloor() + "\n");
            }
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
        advanceState(request);
        return request;
    }

    /**
     * Consumes/dispatches the given request, then advances this Scheduler's state.
     *
     * @param request The request to be dispatched.
     */
    private synchronized void consumeRequest(Request request) {

        switch (state) {

            case DISPATCH_FILE_REQUEST_TO_ELEVATOR, DISPATCH_ELEVATOR_PASSENGER_WAIT_REQUEST_TO_ELEVATOR -> dispatchRequestToElevatorSubsystem(request);

            case DISPATCH_ELEVATOR_DOOR_REQUEST_TO_ELEVATOR -> {
                if (request instanceof ElevatorPassengerWaitRequest) {
                    dispatchRequestToElevatorSubsystem(new ElevatorDoorRequest(Source.SCHEDULER, ElevatorDoorStatus.CLOSED));
                } else if (request instanceof ElevatorDoorRequest) {
                    dispatchRequestToElevatorSubsystem(request);
                }
            }

            case DISPATCH_MOTOR_REQUEST_TO_ELEVATOR -> {
                if (request instanceof ElevatorDestinationRequest) {
                    dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(Source.SCHEDULER, ((ElevatorDestinationRequest) request).getDirection()));
                } else if (request instanceof ElevatorArrivalRequest) {
                    System.out.println(this + " received confirmation of elevator arrival:\n" + request);
                    dispatchRequestToElevatorSubsystem(new ElevatorMotorRequest(Source.SCHEDULER, ElevatorDirection.IDLE));
                }
            }

            case DISPATCH_FILE_REQUEST_TO_FLOOR -> dispatchRequestToFloorSubsystem(request);

            case CONSUME_ELEVATOR_ARRIVAL_REQUEST -> {}

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