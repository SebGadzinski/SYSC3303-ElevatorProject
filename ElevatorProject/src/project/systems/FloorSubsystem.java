package project.systems;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.MatchResult;

import static project.Config.*;

/**
 * Reads requests from a batch file and sends them to the Scheduler; coordinates
 * with the Scheduler on thread-safe queues comprising requests.
 *
 * @author Chase Badalato (iter 3 and 2), Chase Fridgen (iter 2), Paul Roode (iter 1)
 * @version Iteration 3
 */
public class FloorSubsystem extends AbstractSubsystem implements Runnable {

    private Scanner scanner; // for reading request batch files
    private final int floorNo;
    private boolean upLamp;
    private boolean downLamp;

    /**
     * A parameterized FloorSubsystem constructor.
     *
     * @param inetAddress   This FloorSubsystem's IP address.
     * @param inSocketPort  This FloorSubsystem's inlet socket port number.
     * @param outSocketPort This FloorSubsystem's outlet socket port number.
     * @param floorNo       This FloorSubsystem's floor number.
     */
    public FloorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int floorNo) {

        super(inetAddress, inSocketPort, outSocketPort);

        this.downLamp = false;
        this.upLamp = false;
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
                getSource());

        // check for another request
        boolean isThereAnotherRequest;
        if (isThereAnotherRequest = this.scanner.hasNext()) {
            this.scanner.nextLine();
        }

        // multi-return
        return new ReadRequestResult(request, isThereAnotherRequest);

    }

    /**
     * Gets ElevatorDirection from string
     *
     * @param direction ElevatorDirection in string form
     * @return Direction from string
     */
    public ElevatorDirection getDirectionFromString(String direction) {
        if (direction.toLowerCase().trim().equals("up")) {
            return ElevatorDirection.UP;
        } else if (direction.equalsIgnoreCase("down")) {
            return ElevatorDirection.DOWN;
        } else
            return ElevatorDirection.IDLE;
    }

    /**
     * Get subsystem identification
     *
     * @return this subsystems identification
     */
    public SubsystemSource getSource() {
        return new SubsystemSource(SubsystemSource.Subsystem.FLOOR_SUBSYSTEM, Integer.toString(floorNo));
    }

    /**
     * Once a request is received this request must be parsed and handled.  There is a few
     * different packets that can be received and must be dealt with accordingly
     *
     * @param request the request to be dealt with
     */
    public void handleRequest(Request request) {
        System.out.println("\n[FLOOR " + this.floorNo + "] Received a request from SCHEDULER\n");
        if (request instanceof ElevatorArrivalRequest) {
            ElevatorArrivalRequest arrivalRequest = (ElevatorArrivalRequest) request;
            System.out.println(getSource() + "\nElevator Arriving\n");
        } else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            if (doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.OPENED) {
                this.upLamp = false;
                this.downLamp = false;
                System.out.println(getSource() + "\nElevator opening doors\n");
            } else {
                System.out.println(getSource() + "\nElevator closing doors\n ");
            }
        } else System.out.println(getSource() + "\nInvalid Request\n");
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
            if (readRequestResult.getRequest().getOriginFloor() == this.floorNo) {
                if (readRequestResult.getRequest().getOriginFloor() > readRequestResult.getRequest().getDestinationFloor()) {
                    this.downLamp = true;
                } else {
                    this.upLamp = true;
                }
                System.out.println("Sending request to scheduler from floor " + this.floorNo);
                this.sendRequest(readRequestResult.getRequest(), SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
            }
            hasInput = readRequestResult.isThereAnotherRequest();
        }
        while (true) {
            handleRequest(this.waitForRequest());
        }
    }

    public static void main(String[] args) {

        Thread[] floorSubsystemThreads = new Thread[Config.NUMBER_OF_FLOORS];

        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i++) {
            floorSubsystemThreads[i] = new Thread(
                    new FloorSubsystem(
                            FLOORS_UDP_INFO[i].getInetAddress(),
                            FLOORS_UDP_INFO[i].getInSocketPort(),
                            FLOORS_UDP_INFO[i].getOutSocketPort(), i
                    ),
                    ("FloorSubsystem" + i)
            );
            floorSubsystemThreads[i].start();
        }

    }

}
