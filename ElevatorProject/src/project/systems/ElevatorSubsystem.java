package project.systems;

import project.Config;
import project.state_machines.ElevatorState;
import project.state_machines.ElevatorState.ElevatorDirection;
import project.state_machines.ElevatorState.ElevatorDoorStatus;
import project.state_machines.ElevatorState.ElevatorStateStatus;
import project.utils.datastructs.*;
import project.utils.datastructs.ElevatorPassengerWaitRequest.WaitState;
import project.utils.datastructs.Request.Source;
import project.utils.objects.elevator_objects.ElevatorButton;

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
    private ElevatorState state;
    public HashMap<Integer, Boolean> lamps;

    public ElevatorSubsystem(BlockingQueue<Request> incomingRequests, BlockingQueue<Request> outgoingRequests,
            ElevatorState state) {
        this.incomingRequests = incomingRequests;
        this.outgoingRequests = outgoingRequests;
        this.state = state;
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
        if (request instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) request;
            System.out.println(fileRequest.toString());

            handleFileRequest(fileRequest);
        }
        else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            System.out.println("Receiving: " + doorRequest.toString());

            handleDoorRequest(doorRequest);
        }
        else if (request instanceof ElevatorMotorRequest) {
            ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
            System.out.println("Receiving: " + motorRequest.toString());

            handleMotorRequest(motorRequest);
        }
        else if (request instanceof ElevatorPassengerWaitRequest) {
            ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
            System.out.println("Receiving: " + waitRequest.toString());

            handlePassengerWaitRequest(waitRequest);
        } 
        else {
            System.out.println("Invalid Request");
        }
    }

    /**
     * Handles file request, sends a destination request from data in file request
     *
     * @param request The request to be dealt with.
     */
    public void handleFileRequest(FileRequest request) {        
        // Turn on the lamp for the elevator button
        lamps.put(request.getDestinationFloor(), true);

        ElevatorDestinationRequest destinationRequest = new ElevatorDestinationRequest(Source.ELEVATOR_SUBSYSTEM,
                request.getDestinationFloor());
        sendResponse(destinationRequest);
    }

    /**
     * Handles a request to open or close doors. 
     * Sends changed door request back to schedular
     *
     * @param request The request to be dealt with.
     */
    public void handleDoorRequest(ElevatorDoorRequest request) {
        if (request.getRequestedDoorStatus() == ElevatorDoorStatus.CLOSED) {
            System.out.println("Closing Doors");
            waitForTime(Config.ELEVATOR_DOOR_TIME);
            state.setDoorState(ElevatorDoorStatus.CLOSED);
        } else {
            System.out.println("Opening Doors");
            waitForTime(Config.ELEVATOR_DOOR_TIME);
            state.setDoorState(ElevatorDoorStatus.OPENED);
        }
        ElevatorDoorRequest doorRequest = new ElevatorDoorRequest(Source.ELEVATOR_SUBSYSTEM, state.getDoorState());
        sendResponse(doorRequest);
    }

    /**
     * Handles a request to start motor and move up or down. 
     * Sends arrival request back once moved up/donw a floor, or stopped at floor.
     * 
     * @param request The request to be dealt with.
     */
    public void handleMotorRequest(ElevatorMotorRequest request) {
        //Stop sends a motorRequest letting the 
        if (request.getRequestedDirection() == ElevatorDirection.IDLE) {
            System.out.println("Stopping Elevator");
            setUpState(ElevatorDirection.IDLE, ElevatorStateStatus.ELEVATOR_ARRIVAL);
            lamps.put(state.getCurrentFloor(), false);
        }else if (request.getRequestedDirection() == ElevatorDirection.UP) {
            System.out.println("Moving Elevator Up");
            if (state.getCurrentFloor() == Config.NUMBER_OF_FLOORS) {
                System.out.println(
                        "Currently at max floor. Motor request denied. \n Sending a arrival request on max floor");
                ElevatorArrivalRequest arrivalRequest = new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM,
                        state.getCurrentFloor(), state.getDirectionState());
                sendResponse(arrivalRequest);
                return;
            }
            setUpState(ElevatorDirection.UP, ElevatorStateStatus.ELEVATOR_MOVING);
            waitForTime(Config.ELEVATOR_DOOR_TIME);
            state.setCurrentFloor(state.getCurrentFloor() + 1);
        } else {
            System.out.println("Moving Elevator Down");
            if (state.getCurrentFloor() == 0) {
                System.out.println(
                        "Currently at basement floor. Motor request denied. \n Sending a arrival request on basement floor");
                ElevatorArrivalRequest arrivalRequest = new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM,
                        state.getCurrentFloor(), state.getDirectionState());
                sendResponse(arrivalRequest);
                return;
            }
            setUpState(ElevatorDirection.DOWN, ElevatorStateStatus.ELEVATOR_MOVING);
            waitForTime(Config.ELEVATOR_DOOR_TIME);
            state.setCurrentFloor(state.getCurrentFloor() + 1);
        }

        ElevatorArrivalRequest arrivalRequest = new ElevatorArrivalRequest(Source.ELEVATOR_SUBSYSTEM,
        state.getCurrentFloor(), state.getDirectionState());
        System.out.println("Sending : " + arrivalRequest.toString());
        sendResponse(arrivalRequest);
    }

    /**
     * Handles a request to start motor and move up or down. 
     * Sends arrival request back once moved up/donw a floor, or stopped at floor.
     * 
     * @param request The request to be dealt with.
     */
    public void handlePassengerWaitRequest(ElevatorPassengerWaitRequest request) {
        state.setState(ElevatorStateStatus.PASSENGER_HANDLING);
        //Imitate wait time
        waitForTime(request.getWaitTime());

        //Put File Requests (Requests from input file) and Destination Requests to the front of queue
        BlockingQueue<Request> tempQueue = new ArrayBlockingQueue<Request>(incomingRequests.size());
        Boolean isThereFileRequest = false;
        int incomingListSize = incomingRequests.size();
        
        for(int i = 0; i < incomingListSize; i++){
            Request tempRequest = incomingRequests.poll();
            if(tempRequest instanceof FileRequest){
                incomingRequests.offer(tempRequest);
                isThereFileRequest = true;
            }else{
                tempQueue.offer(tempRequest);
            }
        }
        incomingRequests.addAll(tempQueue);

        if(!isThereFileRequest){
            ElevatorPassengerWaitRequest finishedWaitingRequest = new ElevatorPassengerWaitRequest(Source.ELEVATOR_SUBSYSTEM, 0, WaitState.FINISHED);
            sendResponse(finishedWaitingRequest);
        }
    }

    /**
     * Helper function that sets the state and direciton state
     * 
     * @param direction New direction status
     * @param newState New state status
     */
    private void setUpState(ElevatorDirection direction, ElevatorStateStatus newState){
        state.setDirectionState(direction);
        state.setState(newState);
    }

    /**
     * Helper function That allows system to wait for the duration given
     * 
     * @param request The request to be dealt with.
     */
    public void waitForTime(int duration){
        try{
            Thread.sleep(duration);
        }catch (java.lang.InterruptedException e) {
            e.printStackTrace();
        }
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
