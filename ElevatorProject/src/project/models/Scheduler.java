package project.models;

import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Reads input from either elevator and floor subsystems and output to corresponding systems
 *
 * @author Sebastian Gadzinski
 */

public class Scheduler implements Runnable {

    private BlockingQueue<Request> requestsFromElevatorSubsystem;
    private BlockingQueue<Request> requestsToElevatorSubsystem;
    private BlockingQueue<Request> requestsFromFloorSubsystem;
    private BlockingQueue<Request> requestsToFloorSubsystem;
    public FloorSubsystem floorSubsystem;
    public ElevatorSubsystem elevatorSubSystem;

    public Scheduler(BlockingQueue<Request> requestsFromElevatorSubsystem,
                     BlockingQueue<Request> requestsToElevatorSubsystem,
                     BlockingQueue<Request> requestsFromFloorSubsystem,
                     BlockingQueue<Request> requestsToFloorSubsystem,
                     ElevatorSubsystem elevatorSubsystem, FloorSubsystem floorSubsystem) {

        this.requestsFromElevatorSubsystem = requestsFromElevatorSubsystem;
        this.requestsToElevatorSubsystem = requestsToElevatorSubsystem;
        this.requestsFromFloorSubsystem = requestsFromFloorSubsystem;
        this.requestsToFloorSubsystem = requestsToFloorSubsystem;
        this.elevatorSubSystem = elevatorSubsystem;
        this.floorSubsystem = floorSubsystem;

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
     * Retrieves and removes the head of the incoming elevator requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized Request fetchFromElevatorSubsystemRequest() {
        try {
        	Request fetchedRequest = requestsFromElevatorSubsystem.take();
        	System.out.println("Request received by Scheduler from Elevator Subsystem:");
        	
        	if (fetchedRequest instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) fetchedRequest;
                System.out.println("The request was fulfilled at " + fileRequest.getTime());
                System.out.println("The elevator picked up passengers on floor " + fileRequest.getOrginFloor());
                System.out.println("The elevator arrived at floor " + fileRequest.getDestinatinoFloor() + "\n");
        	}
        	
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
    public synchronized Request fetchFromFloorSubsystemRequest() {
        try {
        	Request fetchedRequest = requestsFromFloorSubsystem.take();
        	
            System.out.println("\nRequest received by Scheduler from Floor Subsystem:");
            
            if (fetchedRequest instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) fetchedRequest;
                System.out.println("The request was fulfilled at " + fileRequest.getTime());
                System.out.println("The elevator picked up passengers on floor " + fileRequest.getOrginFloor());
                System.out.println("The elevator arrived at floor " + fileRequest.getDestinatinoFloor() + "\n");
        	}
 
            return fetchedRequest;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void run() {
        System.out.println("Scheduler operational...\n");
        while (true) {
            if (requestsFromElevatorSubsystem.size() > 0) {
            	System.out.println("Schedular - Elvator request...\n");
            	Request fetchedRequest = fetchFromElevatorSubsystemRequest();
                sendRequestToFloorSubsystem(fetchedRequest);
            }
            if (requestsFromFloorSubsystem.size() > 0) {
            	System.out.println("Schedular - Floor Request...\n");
            	Request fetchedRequest = fetchFromFloorSubsystemRequest();
                sendRequestToElevatorSubsystem(fetchedRequest);
            }
        }
    }

}
