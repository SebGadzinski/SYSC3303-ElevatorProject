package project.systems;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.SubsystemSource;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.regex.MatchResult;

import static project.Config.REQUEST_BATCH_FILENAME;
import static project.Config.SCHEDULER_UDP_INFO;

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
    //private Floor floor;
    private int floorNo;

    /**
     * A parameterized constructor.
     *
     * @param inetAddress
     * @param inSocketPort
     * @param outSocketPort
     * @param floorNo
     */
    public FloorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int floorNo) {
        super(inetAddress, inSocketPort, outSocketPort);

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
    public SubsystemSource getSource(){
        return new SubsystemSource(SubsystemSource.Subsystem.FLOOR_SUBSYSTEM, Integer.toString(floorNo));
    }

    public void handleRequest(Request request){
        if(request instanceof ElevatorArrivalRequest){
            ElevatorArrivalRequest arrivalRequest = (ElevatorArrivalRequest) request;
            System.out.println(getSource() + "\nElevator Arriving\n");
        }
        else if(request instanceof ElevatorDoorRequest){
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            if(doorRequest.getRequestedDoorStatus() == ElevatorDoorStatus.OPENED)System.out.println(getSource() + "\nElevator opening doors\n");
            else System.out.println(getSource() + "\nElevator closing doors\n ");
        }
        else System.out.println(getSource() +  "\nInvalid Request\n");
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
                System.out.println("Sending request to scheduler from floor " + this.floorNo);
                //this.sendRequest(readRequestResult.getRequest(), SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
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
            floorSubsystemThreads[i] = new Thread(new FloorSubsystem(Config.FLOORS_UDP_INFO[i].getInetAddress(), Config.FLOORS_UDP_INFO[i].getInSocketPort(), Config.FLOORS_UDP_INFO[i].getOutSocketPort(), i), ("FloorSubsystem" + i));
            floorSubsystemThreads[i].start();
        }
    }

}
