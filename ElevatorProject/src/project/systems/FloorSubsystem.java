package project.systems;

import project.Config;
import project.models.Floor;
import project.state_machines.ElevatorState.ElevatorDirection;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Source;
import project.utils.datastructs.FileRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.MatchResult;

import static project.Config.REQUEST_BATCH_FILENAME;

/**
 * Reads requests from a batch file and sends them to the Scheduler;
 * coordinates with the Scheduler on thread-safe queues comprising requests.
 *
 * @author Paul Roode (Iteration One)
 * @author Chase Badalato (Iteration Two)
 * 
 */
public class FloorSubsystem implements Runnable {

    private BlockingQueue<Request> incomingRequests; // fulfilled requests
    private BlockingQueue<Request> outgoingRequests; // requests to be fulfilled
    private Floor[] floors;
    Scanner scanner; // for reading request batch files

    /**
     * A parameterized constructor.
     *
     * @param incomingRequests Incoming fulfilled requests.
     * @param outgoingRequests Outgoing requests to be fulfilled.
     */
    public FloorSubsystem(BlockingQueue<Request> incomingRequests,
                          BlockingQueue<Request> outgoingRequests) {

        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.floors = new Floor[Config.NUMBER_OF_FLOORS];
        
    	Thread[] floorThreads = new Thread[Config.NUMBER_OF_FLOORS];
        for(int i = 0; i < Config.NUMBER_OF_FLOORS; i ++) {
        	this.floors[i] = new Floor(this.outgoingRequests);
        	floorThreads[i] = new Thread(this.floors[i], ("Thread for floor: " + i));
        	floorThreads[i].start();
        }

        try {
            scanner = new Scanner(new File(Paths.get(REQUEST_BATCH_FILENAME).toAbsolutePath().toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads a request from the request batch file. The stipulated time format is hh:mm:ss.mmm.
     *
     * @return A multi-return auxiliary comprising the read-in request and whether there is another request.
     */
    public synchronized ReadRequestResult readRequest() {

        // match input against a regex
        scanner.findInLine("(\\d+\\S\\d+\\S\\d+\\S\\d\\d\\d) (\\d) ([a-zA-Z]+) (\\d)");
        MatchResult matchResult = scanner.match();

        // store the matched data in a new request instance
        FileRequest request = new FileRequest(matchResult.group(1), Integer.parseInt(matchResult.group(2)), getDirectionFromString(matchResult.group(3)), Integer.parseInt(matchResult.group(4)), Source.FLOOR_SUBSYSTEM);

        // check for another request
        boolean isThereAnotherRequest;
        if (isThereAnotherRequest = scanner.hasNext()) {
            scanner.nextLine();
        }

        // multi-return
        return new ReadRequestResult(request, isThereAnotherRequest);

    }

    /**
     * Inserts the given request into the outgoing request queue,
     * waiting if necessary for space to become available.
     *
     * @param request The request to be inserted into the outgoing request queue.
     */
    public synchronized void sendRequest(Request request) {
    	try {
    		if (request instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) request;
    			this.floors[fileRequest.getOrginFloor()].putRequest(fileRequest);
                System.out.println("FloorSubsystem sent a request to floor " + fileRequest.getOrginFloor());
    		}
    		
    	}
    	catch(IndexOutOfBoundsException e) {
    		if (request instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) request;
    			System.out.println("The requested floor " + fileRequest.getOrginFloor() + " does not exist!");
    		}    		
    		System.out.println("Ignoring this floor ...");
    	}
    }

    /**
     * Retrieves and removes the head of the incoming requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized void fetchRequest() {
        try {
            Request fetchedRequest = incomingRequests.take();
            
            System.out.println("Request received by FloorSubsystem:");
            
            if (fetchedRequest instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) fetchedRequest;
                System.out.println("The request was fulfilled at " + fileRequest.getTime());
                System.out.println("The elevator picked up passengers on floor " + fileRequest.getOrginFloor());
                System.out.println("The elevator arrived at floor " + fileRequest.getDestinatinoFloor() + "\n");
    		}
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public ElevatorDirection getDirectionFromString(String direction) {
    	if (direction.toLowerCase().trim().equals("up")) {
    		return ElevatorDirection.UP;
    	}
    	else if (direction.toLowerCase().equals("down")) return ElevatorDirection.DOWN;
    	else return ElevatorDirection.IDLE;
    }

    /**
     * Reads and transmits requests to be fulfilled, and fetches fulfilled requests.
     */
    @Override
    public void run() {
        System.out.println("FloorSubsystem operational...\n");
        boolean hasInput = true;
        while (hasInput) {
            ReadRequestResult readRequestResult = readRequest();
            sendRequest(readRequestResult.getRequest());
            //fetchRequest();
            hasInput = readRequestResult.isThereAnotherRequest();
        }
        while(true) {
        	
        }
        //System.exit(0);
    }

}


