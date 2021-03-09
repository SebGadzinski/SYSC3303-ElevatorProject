package project.systems;

import static project.Config.REQUEST_BATCH_FILENAME;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.regex.MatchResult;

import project.Config;
import project.models.Floor;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.Request.Source;

/**
 * Reads requests from a batch file and sends them to the Scheduler; coordinates
 * with the Scheduler on thread-safe queues comprising requests.
 *
 * @author Paul Roode (Iteration One)
 * @author Chase Badalato (Iteration Two and Three)
 * @author Chase Fridgen (Iteration Two)
 * @version Iteration 3
 */
public class FloorSubsystem extends AbstractSubsystem implements Runnable {

    private Scanner scanner; // for reading request batch files
    private InetAddress inetAddress;
    //private Floor floor;
    private int floorNo;
    /**
     * A parameterized constructor.
     *
     * @param requestsFromScheduler Incoming fulfilled requests.
     * @param requestsToScheduler   Requests sent
     */
    protected FloorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int floorNo) {
    	super(inetAddress, inSocketPort, outSocketPort);
    	
    	this.inetAddress = inetAddress;
    	//this.floor = new Floor();
    	this.floorNo = floorNo;
    	
        try {
            scanner = new Scanner(new File(Paths.get(REQUEST_BATCH_FILENAME).toAbsolutePath().toString()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Reads a request from the request batch file. The stipulated time format is
     * hh:mm:ss.mmm.
     *
     * @return A multi-return auxiliary comprising the read-in request and whether
     * there is another request.
     */
    public synchronized ReadRequestResult readRequest() {

        // match input against a regex
    	this.scanner.findInLine("(\\d+\\S\\d+\\S\\d+\\S\\d\\d\\d) (\\d) ([a-zA-Z]+) (\\d)");
        MatchResult matchResult = this.scanner.match();

        // store the matched data in a new request instance
        FileRequest request = new FileRequest(matchResult.group(1), Integer.parseInt(matchResult.group(2)),
                getDirectionFromString(matchResult.group(3)), Integer.parseInt(matchResult.group(4)),
                Source.FLOOR_SUBSYSTEM);

        // check for another request
        boolean isThereAnotherRequest;
        if (isThereAnotherRequest = this.scanner.hasNext()) {
        	this.scanner.nextLine();
        }

        // multi-return
        return new ReadRequestResult(request, isThereAnotherRequest);

    }

    public ElevatorDirection getDirectionFromString(String direction) {
        if (direction.toLowerCase().trim().equals("up")) {
            return ElevatorDirection.UP;
        } else if (direction.toLowerCase().equals("down")) {
            return ElevatorDirection.DOWN;
        } else
            return ElevatorDirection.IDLE;
    }

    /**
     * Reads and transmits requests to be fulfilled, and fetches fulfilled requests.
     */
    @Override
    public void run() {
        System.out.println("FloorSubsystem " + this.floorNo + " operational...\n");
        boolean hasInput = true;
        while (hasInput) {
            ReadRequestResult readRequestResult = readRequest();
            //this.sendRequest(readRequestResult.getRequest(), this.inetAddress, 8080);
            hasInput = readRequestResult.isThereAnotherRequest();
        }

        while (true) {
        	this.waitForRequest();
        }
    }
    
    public static void main(String[] args) {
    	int numberOFloors = 1;
    	
    	Thread floorSubsystemThreads[] = new Thread[numberOFloors];
    	InetAddress netty = null;
		try {
			netty = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
    	for(int i = 0; i < numberOFloors; i++) {
    		floorSubsystemThreads[i] = new Thread(new FloorSubsystem(netty, 9001, 9000, i), ("FloorSubsystem" + i));
    		floorSubsystemThreads[i].start();
    	}
    }

}
