package project.systems;

import static project.Config.REQUEST_BATCH_FILENAME;

import java.io.File;
import java.io.FileNotFoundException;
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
 * @author Chase Badalato (Iteration Two)
 * @author Chase Fridgen (Iteration Two)
 * @version Iteration 2
 */
public class FloorSubsystem implements Runnable {

	private BlockingQueue<Request> requestsToScheduler; // requests to be fulfilled
	private BlockingQueue<Request> requestsFromScheduler;
	private Floor[] floors;
	private Thread[] floorThreads;
	Scanner scanner; // for reading request batch files

	public FloorSubsystem() {
		// Testing purposes
	}

	/**
	 * A parameterized constructor.
	 * 
	 * @param incomingRequests    Incoming fulfilled requests.
	 * @param requestsToScheduler Requests sent
	 */
	public FloorSubsystem(BlockingQueue<Request> requestsFromScheduler, BlockingQueue<Request> requestsToScheduler) {

		this.requestsFromScheduler = requestsFromScheduler;
		this.requestsToScheduler = requestsToScheduler;
		this.floors = new Floor[Config.NUMBER_OF_FLOORS];
		this.floorThreads = new Thread[Config.NUMBER_OF_FLOORS];

		for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
			this.floors[i] = new Floor(this.requestsToScheduler);
			this.floorThreads[i] = new Thread(this.floors[i], ("Thread for floor: " + i));
			this.floorThreads[i].start();
		}

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
	 *         there is another request.
	 */
	public synchronized ReadRequestResult readRequest() {

		// match input against a regex
		scanner.findInLine("(\\d+\\S\\d+\\S\\d+\\S\\d\\d\\d) (\\d) ([a-zA-Z]+) (\\d)");
		MatchResult matchResult = scanner.match();

		// store the matched data in a new request instance
		FileRequest request = new FileRequest(matchResult.group(1), Integer.parseInt(matchResult.group(2)),
				getDirectionFromString(matchResult.group(3)), Integer.parseInt(matchResult.group(4)),
				Source.FLOOR_SUBSYSTEM);

		// check for another request
		boolean isThereAnotherRequest;
		if (isThereAnotherRequest = scanner.hasNext()) {
			scanner.nextLine();
		}

		// multi-return
		return new ReadRequestResult(request, isThereAnotherRequest);

	}

	/**
	 * Inserts the given request into the outgoing request queue, waiting if
	 * necessary for space to become available.
	 *
	 * @param request The request to be inserted into the outgoing request queue.
	 */
	public synchronized void sendRequest(Request request) {
		try {
			if (request instanceof FileRequest) {
				FileRequest fileRequest = (FileRequest) request;
				this.floors[fileRequest.getOriginFloor()].putRequest(fileRequest);
				System.out.println("FloorSubsystem sent a request to floor " + fileRequest.getOriginFloor());
			}

		} catch (IndexOutOfBoundsException e) {
			if (request instanceof FileRequest) {
				FileRequest fileRequest = (FileRequest) request;
				System.out.println("The requested floor " + fileRequest.getOriginFloor() + " does not exist!");
			}
			System.out.println("Ignoring this floor ...");
		}
	}

	/**
	 * Retrieves and removes the head of the incoming requests queue, waiting if
	 * necessary until a request becomes available.
	 */
	public synchronized void fetchRequest() {
		try {
			Request fetchedRequest = this.requestsFromScheduler.take();

			if (fetchedRequest instanceof Request) {
				FileRequest fileRequest = (FileRequest) fetchedRequest;
				System.out.println("Request received by FloorSubsystem from " + fileRequest.getSource() + "\n" + fileRequest.toString());
				System.out.println("___________________________________________________________________________________________________");
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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
		System.out.println("FloorSubsystem operational...\n");
		boolean hasInput = true;
		while (hasInput) {
			ReadRequestResult readRequestResult = readRequest();
			sendRequest(readRequestResult.getRequest());
			// fetchRequest();
			hasInput = readRequestResult.isThereAnotherRequest();
		}

		while (true) {
			this.fetchRequest();
		}
//        for(int i = 0; i < this.floors.length; i++) {
//          try {
//              this.floorThreads[i].join();
//          } catch (InterruptedException e) {
//              System.out.println("Could not wait for all floor threads to finish");
//              e.printStackTrace();
//          }
//        }
		// System.exit(0);
	}

}
