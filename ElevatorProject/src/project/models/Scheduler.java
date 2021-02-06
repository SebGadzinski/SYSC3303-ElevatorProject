package project.models;

import project.systems.*;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/*
 * Info:
 * 	Communication between Floor and Elevator
 * 	This is a client to the Scheduler
 * Sending:
 * 	Reply to elevator when work is to be done
 * 	Send data from Elevator to Floor
 * Receiving:
 * 	Input from Floor
 * 	Calls from Elevator
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
    
    //Views the requests from floor subsystem and if anything inside review it and add to elevator
    
    public synchronized void sendRequestToFloorSubsystem(ConcurrentMap<Request.Key, Object> request) {
        try {
        	requestsToFloorSubsystem.put(request);
            System.out.println("Schedueler sent a request to FloorSubsystem\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void sendRequestToElevatorSubsystem(ConcurrentMap<Request.Key, Object> request) {
        try {
        	requestsToElevatorSubsystem.put(request);
            System.out.println("Schedueler sent a request to ElevatorSubsystem\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void fetchFromElevatorSubsystemRequest() {
        try {
            ConcurrentMap<Request.Key, Object> fetchedRequest = requestsFromElevatorSubsystem.take();
            System.out.println("Request received by Scheduler from Elevator Subsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public synchronized void fetchFromFloorSubsystemRequest() {
        try {
            ConcurrentMap<Request.Key, Object> fetchedRequest = requestsFromFloorSubsystem.take();
            System.out.println("Request received by Scheduler from Floor Subsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    //Views the requests from elevator class and if anything 

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
        	while(requestsFromElevatorSubsystem.size() <= 0 && requestsFromFloorSubsystem.size() <= 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    return;
                }
        	}
        	if(requestsFromElevatorSubsystem.size() > 0) {
        		
        	}
        	if(requestsFromFloorSubsystem.size() > 0) {
        		
        	}
        }
    }

}
