package project.models;

import project.systems.*;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Reads input from either elevator and floor subsystems and output to corresponding systems
 *
 * @author Sebastian Gadzinski
 */

public class Scheduler implements Runnable {

    private BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem;
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem;
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem;
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem;
    public FloorSubsystem floorSubsystem;
    public ElevatorSubsystem elevatorSubSystem;
    private int sizeOfIncomingRequestsFloorSubSystem = 0;
    private int sizeOfIncomingRequestsElevatorSubSystem = 0;
    //private static State state;

    /* <!-- not sure if needed for iter1 -Paul
    public enum State {
        WAITING_FOR_REQUEST
        // etc.
    }
    */

    public Scheduler(BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem,
					 BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem,
					 BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem,
					 BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem,
					 ElevatorSubsystem elevatorSubsystem, FloorSubsystem floorSubsystem) {
    	this.requestsFromElevatorSubsystem = requestsFromElevatorSubsystem;
    	this.requestsToElevatorSubsystem = requestsToElevatorSubsystem;
    	this.requestsFromFloorSubsystem = requestsFromFloorSubsystem;
    	this.requestsToFloorSubsystem = requestsToFloorSubsystem;
    	this.elevatorSubSystem = elevatorSubsystem;
    	this.floorSubsystem = floorSubsystem;
        // init queue fields etc.
        //state = State.WAITING_FOR_REQUEST;
    }
    
    /**
     * Inserts the given request into the outgoing floor request queue,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outgoing floor request queue.
     */
    public synchronized void sendRequestToFloorSubsystem(ConcurrentMap<Request.Key, Object> request) {
        try {
        	requestsToFloorSubsystem.put(request);
            System.out.println("Schedueler sent a request to FloorSubsystem\n");
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
    public synchronized void sendRequestToElevatorSubsystem(ConcurrentMap<Request.Key, Object> request) {
        try {
        	requestsToElevatorSubsystem.put(request);
            System.out.println("Schedueler sent a request to ElevatorSubsystem\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retrieves and removes the head of the incoming elevator requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized ConcurrentMap<Request.Key, Object> fetchFromElevatorSubsystemRequest() {
        try {
            ConcurrentMap<Request.Key, Object> fetchedRequest = requestsFromElevatorSubsystem.take();
            System.out.println("Request received by Scheduler from Elevator Subsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
            return fetchedRequest;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Retrieves and removes the head of the incoming floor requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized ConcurrentMap<Request.Key, Object> fetchFromFloorSubsystemRequest() {
        try {
            ConcurrentMap<Request.Key, Object> fetchedRequest = requestsFromFloorSubsystem.take();
            System.out.println("Request received by Scheduler from Floor Subsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
            return fetchedRequest;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Gets the state of this Scheduler.
     * @return The state of this Scheduler.
     */
    //public static State getState() {
    //    return state;
    //}
    
    @Override
    public void run() {
    	System.out.println("Scheduler operational...\n");
    	boolean running = true;
        while (running) {
        	if(requestsFromElevatorSubsystem.size() > 0) {
				ConcurrentMap<Request.Key, Object> fetchedRequest = fetchFromElevatorSubsystemRequest();
				sendRequestToFloorSubsystem(fetchedRequest);
        	}
        	if(requestsFromFloorSubsystem.size() > 0) {
					ConcurrentMap<Request.Key, Object> fetchedRequest = fetchFromFloorSubsystemRequest();
					sendRequestToElevatorSubsystem(fetchedRequest);
        	}
        }
    }

}
