package project.systems;

import project.state_machines.ElevatorState;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Source;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Receives data from the scheduler and
 * then sends it right back
 *
 * @author Chase Badalato
 */
public class ElevatorSubsystem implements Runnable {

    private BlockingQueue<Request> incomingRequests; // data from scheduler
    private BlockingQueue<Request> outgoingRequests; // data to scheduler
    private ElevatorState state;
    
    
    public ElevatorSubsystem(BlockingQueue<Request> incomingRequests,
                             BlockingQueue<Request> outgoingRequests, ElevatorState state) {

        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.state = state;

    }

    /**
     * send the received data back to the scheduler
     *
     * @param response the data to send to the scheduler
     */
    public synchronized void sendResponse(Request response) {
        try {
            outgoingRequests.put(response);
            System.out.println("ElevatorSubsystem responded to Scheduler\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * Wait until the queue receives a packet where this thread will be notified, wake up,
     * and then parse the packet
     *
     * @return the received packet
     */
    public synchronized Request fetchRequest() {
        try {
        	Request fetchedRequest = incomingRequests.take();
            System.out.println("Request received by ElevatorSubsystem:");

            if(fetchedRequest instanceof FileRequest) {
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
    
    public synchronized void handleRequest(Request request) {
    	if(request instanceof FileRequest) {
    		
    		FileRequest fileRequest = (FileRequest) request;
    		
    		//Change source as i am now sending from Elevator Subsystem
    		fileRequest.setSource(Source.ELEVATOR_SUBSYSTEM);
    		
            sendResponse(fileRequest);
    	}
    }

    /**
     * Attempts to fetch a packet. When this gets fetched,
     * sends the response to the scheduler.
     */
    @Override
    public void run() {
        System.out.println("ElevatorSubsystem operational...\n");
        while (true) {
        	Request fetchedRequest = fetchRequest();
            handleRequest(fetchedRequest);
        }
    }
}
