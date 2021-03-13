package project.systems;

import static project.Config.*;

import project.Config;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.utils.datastructs.*;
import project.utils.datastructs.SubsystemSource.Subsystem;

import java.net.InetAddress;
import java.util.HashMap;

/**
 * Receives data from the scheduler and then sends it right back
 *
 * @author Iter1 (Chase Badalato), Iter2 (Sebastian Gadzinski), Iter3 (Sebastian Gadzinski)
 */
public class ElevatorSubsystem extends AbstractSubsystem implements Runnable {

    private ElevatorStateMachine stateMachine;
    public HashMap<Integer, Boolean> lamps;
    public int elevatorNumber; 

    public ElevatorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int elevatorNumber) {
    	super(inetAddress, inSocketPort, outSocketPort);
        this.stateMachine = new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED,
            ElevatorDirection.IDLE, 0, new HashMap<Integer, Boolean>());
        this.elevatorNumber = elevatorNumber;
    }

    /**
     * send the received data back to the scheduler
     *
     * @param response the data to send to the scheduler
     */
    public synchronized void sendResponse(Request response) {
        sendRequest(response, SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
        System.out.println(getSource() + "\nResponded to Scheduler: \n " + response);
    }

    /**
     * Wait until the queue receives a packet where this thread will be notified,
     * wake up, and then parse the packet
     *
     * @return the received packet
     */
    public synchronized Request fetchRequest() {
        Request fetchedRequest = waitForRequest();
        return fetchedRequest;
    }

    /**
     * Identifies request and sends to proper handler
     *
     * @param request received packet
     */
    public synchronized void handleRequest(Request request) {
        Request response = null;
        String reponseString = "\nRequest received by:\n" + getSource() + "\n";
        if (request instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) request;
            System.out.println(reponseString + fileRequest);

            response = handleFileRequest(fileRequest);
        } 
        else if (request instanceof ElevatorDestinationRequest) {
            ElevatorDestinationRequest destinatinonRequest = (ElevatorDestinationRequest) request;
            // Turn on the lamp for the elevator button
            System.out.println(reponseString + destinatinonRequest);
            setLampStatus(destinatinonRequest.getRequestedDestinationFloor(), true);
            response = request;
        } 
        else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            System.out.println(reponseString + doorRequest);

            response = stateMachine.handleRequest(doorRequest);

        } else if (request instanceof ElevatorMotorRequest) {
            ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
            System.out.println(reponseString + motorRequest);

            response = stateMachine.handleRequest(motorRequest);
        } else if (request instanceof ElevatorPassengerWaitRequest) {
            ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
            System.out.println(reponseString + waitRequest);

            response = stateMachine.handleRequest(waitRequest);
        }
        if (response != null) {
            response.setSource(getSource());            
            sendResponse(response);
        }
    }

    /**
     * Handles file request, sends a destination request from data in file request
     *
     * @param request The request to be dealt with.
     */
    public Request handleFileRequest(FileRequest request) {
        // Turn on the lamp for the elevator button
        // setLampStatus(request.getOriginFloor(), true);

        // notifyAll();

        return new ElevatorDestinationRequest(new SubsystemSource(Subsystem.ELEVATOR_SUBSYSTEM, Integer.toString(elevatorNumber)),
                request.getOriginFloor(),
                request.getDirection());
    }

    /**
     * Get subsystem identification
     * 
     * @return this subsystems identification
     */
    public SubsystemSource getSource(){
        return new SubsystemSource(SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM, Integer.toString(elevatorNumber));
    }

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
     * 
     */
    @Override
    public void run() {
        System.out.println("ElevatorSubsystem: " + elevatorNumber +  " operational...\n");

        while (true) {
            Request fetchedRequest = fetchRequest();
            handleRequest(fetchedRequest);
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < Config.NUMBER_OF_ELEVATORS; i++) {
            ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(
                    Config.ELEVATORS_UDP_INFO[i].getInetAddress(),
                    Config.ELEVATORS_UDP_INFO[i].getInSocketPort(),
                    Config.ELEVATORS_UDP_INFO[i].getOutSocketPort(), 
                    i
            ); //Config.ELEVATORS_UDP_INFO[i].getOutSocketPort()
            Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "elevator#" + i);
            elevatorSubsystemThread.start();
        }
    }
}
