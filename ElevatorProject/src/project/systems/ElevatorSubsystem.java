package project.systems;

import static project.Config.*;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Receives data from the scheduler and then sends it right back
 *
 * @author Iter1 (Chase Badalato), Iter2 (Sebastian Gadzinski)
 */
public class ElevatorSubsystem extends AbstractSubsystem implements Runnable {

    private ElevatorStateMachine stateMachine;
    public HashMap<Integer, Boolean> lamps;

    public ElevatorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
    	super(inetAddress, inSocketPort, outSocketPort);
        this.stateMachine = new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED,
			ElevatorDirection.IDLE, 1, new HashMap<Integer, Boolean>());
    }

    /**
     * send the received data back to the scheduler
     *
     * @param response the data to send to the scheduler
     */
    public synchronized void sendResponse(Request response) throws InterruptedException {
        sendRequest(response, SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
        System.out.println("ElevatorSubsystem responded to Scheduler\n");
    }

    /**
     * Wait until the queue receives a packet where this thread will be notified,
     * wake up, and then parse the packet
     *
     * @return the received packet
     */
    public synchronized Request fetchRequest() {
        Request fetchedRequest = waitForRequest();
        System.out.println("Request received by ElevatorSubsystem:");

        return fetchedRequest;
    }

    /**
     * Identifies request and sends to proper handler
     *
     * @param request received packet
     */
    public synchronized void handleRequest(Request request) {
        Request response = null;
        if (request instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) request;
            System.out.println(fileRequest);

            response = handleFileRequest(fileRequest);
        } else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            System.out.println("Receiving: \n" + doorRequest.toString());

            response = stateMachine.handleRequest(doorRequest);

            if (stateMachine.noMoreDestinations()) {
                stateMachine.setState(ElevatorState.IDLE);
            }

            //As long as its not a fault request, check for any more file requests, if none set state to IDLE
            // if (!(request instanceof ElevatorFaultRequest) && stateMachine.getState() == ElevatorState.CLOSING_DOORS) {
            //     boolean noFileRequests = reOrderQueue();

            //     if (noFileRequests && stateMachine.noMoreDestinations()) {
            //         stateMachine.setState(ElevatorState.IDLE);
            //     }
            // }
        } else if (request instanceof ElevatorMotorRequest) {
            ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
            System.out.println("Receiving: \n" + motorRequest.toString());

            response = stateMachine.handleRequest(motorRequest);
        } else if (request instanceof ElevatorPassengerWaitRequest) {
            ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
            System.out.println("Receiving: \n" + waitRequest.toString());

            response = stateMachine.handleRequest(waitRequest);
        }
        if (response != null) {
            System.out.println("Response: \n" + response.toString());
            sendRequest(response, SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
        }
    }

    /**
     * Handles file request, sends a destination request from data in file request
     *
     * @param request The request to be dealt with.
     */
    public Request handleFileRequest(FileRequest request) {
        // Turn on the lamp for the elevator button
        setLampStatus(request.getDestinationFloor(), true);
        stateMachine.putDestinationQueue(request);

        notifyAll();

        return new ElevatorDestinationRequest(Source.ELEVATOR_SUBSYSTEM,
                request.getDestinationFloor(),
                request.getDirection());
    }

    // /**
    //  * Reorder the queue so that fileRequests are at the front in order to create destination requests (Button presses)
    //  */
    // public boolean reOrderQueue() {
    //     int incomingListSize = incomingRequests.size();
    //     boolean noFileRequests = true;

    //     if (incomingListSize > 0) {
    //         BlockingQueue<Request> tempQueue = new ArrayBlockingQueue<Request>(incomingRequests.size());

    //         for (int i = 0; i < incomingListSize; i++) {
    //             Request tempRequest = incomingRequests.poll();
    //             if (tempRequest instanceof FileRequest) {
    //                 incomingRequests.offer(tempRequest);
    //                 noFileRequests = false;
    //             } else {
    //                 tempQueue.offer(tempRequest);
    //             }
    //         }
    //         incomingRequests.addAll(tempQueue);
    //     }
    //     return noFileRequests;
    // }

    /**
     * Sets a lamps state, notifies other threads about the change
     *
     * @param floor  floor button lamp
     * @param status status to be set
     */
    public void setLampStatus(int floor, boolean status) {
        stateMachine.setLampStatus(floor, status);
        notifyAll();
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

    public static void main(String[] args) {


    }


}
