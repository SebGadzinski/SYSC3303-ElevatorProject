package project.systems;

import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.utils.datastructs.*;
import project.utils.datastructs.Request.Source;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Receives data from the scheduler and then sends it right back
 *
 * @author Iter1 (Chase Badalato), Iter2 (Sebastian Gadzinski)
 */
public class ElevatorSubsystem implements Runnable {

    private BlockingQueue<Request> incomingRequests; // data from scheduler
    private BlockingQueue<Request> outgoingRequests; // data to scheduler
    private ElevatorStateMachine stateMachine;
    public HashMap<Integer, Boolean> lamps;

    public ElevatorSubsystem(BlockingQueue<Request> incomingRequests, BlockingQueue<Request> outgoingRequests,
            ElevatorStateMachine stateMachine) {
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.stateMachine = stateMachine;
    }

    /**
     * send the received data back to the scheduler
     *
     * @param response the data to send to the scheduler
     */
    public synchronized void sendResponse(Request response) {
        try {
            outgoingRequests.put(response);
            System.out.println("ElevatorSubsystem responded to Scheduler\n");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wait until the queue receives a packet where this thread will be notified,
     * wake up, and then parse the packet
     *
     * @return the received packet
     */
    public synchronized Request fetchRequest() {
        try {
            Request fetchedRequest = incomingRequests.take();
            System.out.println("Request received by ElevatorSubsystem:");

            return fetchedRequest;

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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
            System.out.println(fileRequest.toString());

            response = handleFileRequest(fileRequest);
        }
        else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            System.out.println("Receiving: " + doorRequest.toString());
            
            response = stateMachine.handleRequest(doorRequest);

            //As long as its not a fault request, check for any more file requests, if none set state to IDLE
            if(request instanceof ElevatorFaultRequest){
                boolean noFileRequests = reOrderQueue();

                if(noFileRequests){
                    stateMachine.setState(ElevatorState.IDLE);
                }
            }
        }
        else if (request instanceof ElevatorMotorRequest) {
            ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
            System.out.println("Receiving: " + motorRequest.toString());

            response = stateMachine.handleRequest(motorRequest);
        }
        else if (request instanceof ElevatorPassengerWaitRequest) {
            ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
            System.out.println("Receiving: " + waitRequest.toString());

            response = stateMachine.handleRequest(waitRequest); 
        } 
        if(response != null) sendResponse(response);
    }

    /**
     * Handles file request, sends a destination request from data in file request
     *
     * @param request The request to be dealt with.
     */
    public Request handleFileRequest(FileRequest request) {        
        // Turn on the lamp for the elevator button
        setLampStatus(request.getDestinationFloor(), true);
        notifyAll();

        return new ElevatorDestinationRequest(Source.ELEVATOR_SUBSYSTEM,
                request.getDestinationFloor());
    }

    /**
     * Reorder the queue so that fileRequests are at the front in order to create destination requests (Button presses)
     */
    public boolean reOrderQueue(){
        BlockingQueue<Request> tempQueue = new ArrayBlockingQueue<Request>(incomingRequests.size());
        int incomingListSize = incomingRequests.size();
        boolean noFileRequests = true;
        
        for(int i = 0; i < incomingListSize; i++){
            Request tempRequest = incomingRequests.poll();
            if(tempRequest instanceof FileRequest){
                incomingRequests.offer(tempRequest);
                noFileRequests = false;
            }else{
                tempQueue.offer(tempRequest);
            }
        }
        incomingRequests.addAll(tempQueue);
        return noFileRequests;
    }

    public void setLampStatus(int floor, boolean status){
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
}
