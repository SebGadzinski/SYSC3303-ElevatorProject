package project.models;

import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Source;

import java.util.concurrent.BlockingQueue;

/**
 * Reads input from either elevator and floor subsystems and output to corresponding systems
 *
 * @author Sebastian Gadzinski
 */

public class Scheduler implements Runnable {

    private BlockingQueue<Request> requestsFromSubsystems;
    private BlockingQueue<Request> requestsToElevatorSubsystem;
    private BlockingQueue<Request> requestsToFloorSubsystem;

    public Scheduler(BlockingQueue<Request> requestsFromSubsystems,
                     BlockingQueue<Request> requestsToElevatorSubsystem,
                     BlockingQueue<Request> requestsToFloorSubsystem) {

        this.requestsFromSubsystems = requestsFromSubsystems;
        this.requestsToElevatorSubsystem = requestsToElevatorSubsystem;
        this.requestsToFloorSubsystem = requestsToFloorSubsystem;
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
    public synchronized Request fetchSubsystemRequest() {
        try {
        	Request fetchedRequest = requestsFromSubsystems.take();
     
    		System.out.println("Request received by Scheduler from: " + fetchedRequest.getSource());
        	
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
        	Request fetchedRequest = fetchSubsystemRequest();
        	if (fetchedRequest.getSource() == Source.ELEVATOR_SUBSYSTEM) {
                sendRequestToFloorSubsystem(fetchedRequest);
            }else {
            	sendRequestToElevatorSubsystem(fetchedRequest);
            }
        }
    }

}
