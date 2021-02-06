package project.systems;

import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Receives data from the scheduler and
 * then sends it right back
 * 
 * @author Chase Badalato
 *
 */
public class ElevatorSubsystem implements Runnable {

    private BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests; // data from scheduler
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests; // data to scheduler
    
    public ElevatorSubsystem(BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests,
                             BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests) {

    	this.incomingRequests = incomingRequests;
    	this.outgoingRequests = outgoingRequests;
    	
    }

    /**
     * 
     * @param response the data to send to the scheduler
     */
    public synchronized void sendResponse(ConcurrentMap<Request.Key, Object> response) {
        try {
            outgoingRequests.put(response);
            System.out.println("ElevatorSubsystem responded to Scheduler\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    public synchronized ConcurrentMap<Request.Key, Object> fetchRequest() {
        try {
            ConcurrentMap<Request.Key, Object> fetchedRequest = incomingRequests.take();
            System.out.println("Request received by ElevatorSubsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
            
            return fetchedRequest;
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
        
    }

    @Override
    public void run() {
        System.out.println("Elevator System up and running ...\n");
        while (true) {
        	ConcurrentMap<Request.Key, Object> fetchedRequest = fetchRequest();
            sendResponse(fetchedRequest);
            fetchRequest();
            System.out.println("ElevatorSubsystem forwarded packet to scheduler");
        }
    }

}
