package project.systems;

import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
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

    private BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests; // fulfilled requests
    private BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests; // requests to be fulfilled
    private ArrayList<Request> requests = new ArrayList<Request>();
    Scanner scanner; // for reading request batch files

    /**
     * A parameterized constructor.
     *
     * @param incomingRequests Incoming fulfilled requests.
     * @param outgoingRequests Outgoing requests to be fulfilled.
     * @throws FileNotFoundException 
     */
    public FloorSubsystem(BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests,
                          BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests) throws FileNotFoundException {

        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        
        String path = Paths.get(REQUEST_BATCH_FILENAME).toAbsolutePath().toString();
        
        scanner = new Scanner(new File(path));

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
        if (scanner.hasNext()) {
            scanner.nextLine();
            isThereAnotherRequest = true;
        } else {
            isThereAnotherRequest = false;
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
            ConcurrentMap<Request.Key, Object> fetchedRequest = incomingRequests.take();
            System.out.println("Request received by FloorSubsystem:");
            System.out.println("The request was fulfilled at " + fetchedRequest.get(Request.Key.TIME));
            System.out.println("The elevator picked up passengers on floor " + fetchedRequest.get(Request.Key.ORIGIN_FLOOR));
            System.out.println("The elevator arrived at floor " + fetchedRequest.get(Request.Key.DESTINATION_FLOOR) + "\n");
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
            sendRequest(readRequestResult.getRequest());
            fetchRequest();
            hasInput = readRequestResult.isThereAnotherRequest();
        }
        System.exit(0);
    }

}


