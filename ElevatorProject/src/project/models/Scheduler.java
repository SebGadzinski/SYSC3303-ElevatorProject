package project.models;

import project.state_machines.SchedulerStateMachine.SchedulerState;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;

/**
 * Reads input from either elevator and floor subsystems and output to corresponding systems
 *
 * @author Paul Roode, Sebastian Gadzinski
 */

public class Scheduler implements Runnable {

    private BlockingQueue<Request> requestsFromSubsystems;
    private BlockingQueue<Request> requestsToElevatorSubsystem;
    private BlockingQueue<Request> requestsToFloorSubsystem;

    private SchedulerState state;

    public Scheduler(BlockingQueue<Request> requestsFromSubsystems,
                     BlockingQueue<Request> requestsToElevatorSubsystem,
                     BlockingQueue<Request> requestsToFloorSubsystem) {

        this.requestsFromSubsystems = requestsFromSubsystems;
        this.requestsToElevatorSubsystem = requestsToElevatorSubsystem;
        this.requestsToFloorSubsystem = requestsToFloorSubsystem;
        this.state = SchedulerState.AWAIT_REQUEST;

    }

    /**
     * Inserts the given request into the outgoing floor request queue,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outgoing floor request queue.
     */
    public synchronized void sendRequestToFloorSubsystem(Request request) {
        try {
            requestsToFloorSubsystem.put(request);
            System.out.println("Scheduler sent a request to FloorSubsystem\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts the given request into the outgoing elevator request queue,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outgoing elevator request queue.
     */
    public synchronized void sendRequestToElevatorSubsystem(Request request) {
        try {
            requestsToElevatorSubsystem.put(request);
            System.out.println("Scheduler sent a request to ElevatorSubsystem\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves and removes the head of the incoming subsystems requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized Request fetchRequest() {
        try {
            Request fetchedRequest = requestsFromSubsystems.take();
            System.out.println("Request received by Scheduler from " + fetchedRequest.getSource() + ":");
            if (fetchedRequest instanceof FileRequest) {
                FileRequest fileRequest = (FileRequest) fetchedRequest;
                System.out.println("The request was fulfilled at " + fileRequest.getTime());
                System.out.println("The elevator picked up passengers on floor " + fileRequest.getOriginFloor());
                System.out.println("The elevator arrived at floor " + fileRequest.getDestinationFloor() + "\n");
            }
            return fetchedRequest;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Dispatches the given request.
     *
     * @param request The request to be dispatched.
     */
    private synchronized void dispatchRequest(Request request) {
        switch (state) {
            case DISPATCH_FILE_REQUEST_TO_ELEVATOR -> sendRequestToElevatorSubsystem(request);
            case DISPATCH_FILE_REQUEST_TO_FLOOR -> sendRequestToFloorSubsystem(request);
            case INVALID_REQUEST -> System.out.println(toString() + " received and discarded an invalid request");
        }
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
    private void advanceState(Request request) {
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
     * Drives this Scheduler.
     */
    @Override
    public void run() {

        System.out.println("Scheduler operational...\n");
        Request request;

        while (true) {

            if (!requestsFromSubsystems.isEmpty()) {

                // fetch a request
                request = fetchRequest();
                advanceState(request);

                // dispatch the fetched request
                dispatchRequest(request);
                advanceState(request);

            }

        }

    }

}