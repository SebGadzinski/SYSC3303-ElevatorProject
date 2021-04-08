package project.systems;

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
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.*;
import static project.utils.datastructs.SubsystemSource.Subsystem.FLOOR_SUBSYSTEM;

/**
 * Reads requests from a batch file and sends them to the Scheduler; coordinates
 * with the Scheduler on thread-safe queues comprising requests.
 *
 * @author Chase Badalato
 * @author Chase Fridgen
 * @author Paul Roode
 * @version Iteration 5
 */
public class FloorSubsystem extends AbstractSubsystem {

    private Scanner scanner; // for reading request batch files
    private final int floorNo;
    private boolean upLamp, downLamp;
    private final UDPInfo schedulerUDPInfo;

    /**
     * A parameterized constructor of a FloorSubsystem.
     *
     * @param inetAddress      The socket IP address.
     * @param inSocketPort     The inlet socket port number.
     * @param outSocketPort    The outlet socket port number.
     * @param floorNo          The floor number.
     * @param schedulerUDPInfo The UDP info for the Scheduler.
     */
    public FloorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int floorNo, UDPInfo schedulerUDPInfo) {

        super(inetAddress, inSocketPort, outSocketPort);
        this.floorNo = floorNo;
        this.schedulerUDPInfo = schedulerUDPInfo;
        downLamp = false;
        upLamp = false;

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
        this.scanner.findInLine("(\\d+\\S\\d+\\S\\d+\\S\\d\\d\\d) (\\d\\d) ([a-zA-Z]+) (\\d\\d) (\\d)");
        MatchResult matchResult = this.scanner.match();

        // store the matched data in a new request instance
        FileRequest request = new FileRequest(matchResult.group(1), Integer.parseInt(matchResult.group(2)),
                getDirectionFromString(matchResult.group(3)), Integer.parseInt(matchResult.group(4)), getSource(), Integer.parseInt(matchResult.group(5)));

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
            return UP;
        } else if (direction.equalsIgnoreCase("down")) {
            return DOWN;
        } else
            return IDLE;
    }

    /**
     * Get subsystem identification
     *
     * @return this subsystems identification
     */
    public SubsystemSource getSource() {
        return new SubsystemSource(FLOOR_SUBSYSTEM, Integer.toString(floorNo));
    }

    /**
     * Once a request is received this request must be parsed and handled. There is
     * a few different packets that can be received and must be dealt with
     * accordingly
     *
     * @param request the request to be dealt with
     */
    public void handleRequest(Request request) {
        System.out.println("\nTimeStamp: " + getTimestamp());
        System.out.println("[FLOOR " + this.floorNo + "] Received a request from SCHEDULER\n");
        if (request instanceof ElevatorArrivalRequest) {
            ElevatorArrivalRequest arrivalRequest = (ElevatorArrivalRequest) request;
            System.out.println(getSource() + "\nElevator Arriving\n");
        } else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            if (doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.OPENED) {
                this.upLamp = false;
                this.downLamp = false;
                System.out.println("\nElevator opening doors\n");
            } else {
                System.out.println("\nElevator closing doors\n ");
            }
        } else if (request instanceof FloorEmergencyRequest) {
            System.out.println("Floor " + this.floorNo + "shutting down");
            System.exit(1);
        } else
            System.out.println("\nInvalid Request\n");
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
                System.out.println("\nTimeStamp: " + getTimestamp());
                System.out.println("Sending request to scheduler from floor " + this.floorNo);
                this.sendRequest(readRequestResult.getRequest(), schedulerUDPInfo.getInetAddress(), schedulerUDPInfo.getInSocketPort());
            }
            hasInput = readRequestResult.isThereAnotherRequest();
        }
        while (true) {
            handleRequest(this.waitForRequest());
        }
    }

    /**
     * Initializes and starts the FloorSubsystem threads.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        for (int i = 0; i < NUMBER_OF_FLOORS; ++i) {
            UDPInfo floorUDPInfo = FLOORS_UDP_INFO[i];
            FloorSubsystem floorSubsystem = new FloorSubsystem(
                    floorUDPInfo.getInetAddress(),
                    floorUDPInfo.getInSocketPort(),
                    floorUDPInfo.getOutSocketPort(),
                    i,
                    SCHEDULER_UDP_INFO
            );
            Thread floorSubsystemThread = new Thread(floorSubsystem, "FloorSubsystem " + i);
            floorSubsystemThread.start();
        }
    }

}