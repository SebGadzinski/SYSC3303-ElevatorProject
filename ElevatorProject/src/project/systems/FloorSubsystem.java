package project.systems;

<<<<<<< Updated upstream
=======
import project.Config;
import project.models.Floor;
import project.utils.datastructs.HashMapCompare;
>>>>>>> Stashed changes
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.MatchResult;

import static project.Config.REQUEST_BATCH_FILENAME;

/**
 * Reads requests from a batch file and sends them to the Scheduler;
 * coordinates with the Scheduler on thread-safe queues comprising requests.
 *
 * @author Paul Roode
 */
public class FloorSubsystem implements Runnable {

<<<<<<< Updated upstream
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests; // fulfilled requests
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests; // requests to be fulfilled
=======
    private BlockingQueue<Request> requestsToScheduler; // requests to be fulfilled
    private BlockingQueue<Request> requestsFromScheduler; // requests to be fulfilled

    private Floor[] floors;
    private Thread[] floorThreads;
>>>>>>> Stashed changes
    Scanner scanner; // for reading request batch files
    private ArrayList<ConcurrentMap<Request.Key, Object>> tempList;

    /**
     * A parameterized constructor.
     *
     * @param incomingRequests Incoming fulfilled requests.
     * @param outgoingRequests Outgoing requests to be fulfilled.
     */
<<<<<<< Updated upstream
    public FloorSubsystem(BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests,
                          BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests) {

<<<<<<< Updated upstream
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
=======
    public FloorSubsystem(BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToScheduler) {

    	this.tempList = new ArrayList<ConcurrentMap<Request.Key, Object>>();
=======
    	this.requestsFromScheduler = incomingRequests;
>>>>>>> Stashed changes
        this.requestsToScheduler = requestsToScheduler;
        this.floors = new Floor[Config.NUMBER_OF_FLOORS];
    	this.floorThreads = new Thread[Config.NUMBER_OF_FLOORS];
        for(int i = 0; i < Config.NUMBER_OF_FLOORS; i ++) {
        	this.floors[i] = new Floor(this.requestsToScheduler);
        	floorThreads[i] = new Thread(this.floors[i], ("Thread for floor: " + i));
        	floorThreads[i].start();
        }
>>>>>>> Stashed changes

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
        ConcurrentMap<Request.Key, Object> request = Request.newInstance();
        request.put(Request.Key.TIME, matchResult.group(1));
        request.put(Request.Key.ORIGIN_FLOOR, Integer.parseInt(matchResult.group(2)));
        request.put(Request.Key.DIRECTION, matchResult.group(3));
        request.put(Request.Key.DESTINATION_FLOOR, Integer.parseInt(matchResult.group(4)));

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
    public synchronized void sendRequest(ConcurrentMap<Request.Key, Object> request) {
        try {
            outgoingRequests.put(request);
            System.out.println("FloorSubsystem sent a request\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves and removes the head of the incoming requests queue,
     * waiting if necessary until a request becomes available.
     */
    public synchronized void fetchRequest() {
        try {
<<<<<<< Updated upstream
            ConcurrentMap<Request.Key, Object> fetchedRequest = incomingRequests.take();
            System.out.println("Request received by FloorSubsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
=======
            Request fetchedRequest = this.requestsFromScheduler.take();
            System.out.println("Request received by FloorSubsystem:");
            
            if (fetchedRequest instanceof FileRequest) {
    			FileRequest fileRequest = (FileRequest) fetchedRequest;
    			System.out.println("Received Request from " + fileRequest.getSource());
    		}
            
>>>>>>> Stashed changes
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
            //this.tempList.add(readRequestResult.getRequest());
            sendRequest(readRequestResult.getRequest());
            fetchRequest();
            hasInput = readRequestResult.isThereAnotherRequest();
        }
<<<<<<< Updated upstream
<<<<<<< Updated upstream
        System.exit(0);
=======

        for(int i = 0; i < this.floors.length; i++) {
        	try {
				this.floorThreads[i].join();
			} catch (InterruptedException e) {
				System.out.println("Could not wait for all floor threads to finish");
				e.printStackTrace();
			}
=======
        while (true) {
        	this.fetchRequest();
//	        for(int i = 0; i < this.floors.length; i++) {
//	        	try {
//					this.floorThreads[i].join();
//				} catch (InterruptedException e) {
//					System.out.println("Could not wait for all floor threads to finish");
//					e.printStackTrace();
//				}
//	        }
>>>>>>> Stashed changes
        }
        //System.exit(0);
>>>>>>> Stashed changes
    }

}


