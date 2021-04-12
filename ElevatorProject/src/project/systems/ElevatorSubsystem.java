package project.systems;

import project.Config;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.utils.datastructs.*;
import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;
import project.utils.objects.general.CreateFile;

import java.util.HashMap;

import static project.Config.*;
import static project.utils.datastructs.ElevatorEmergencyRequest.COMPLETED_EMERGENCY;
import static project.utils.datastructs.SubsystemSource.Subsystem.ELEVATOR_SUBSYSTEM;

/**
 * Receives data from the scheduler and then sends it right back
 *
 * @author Chase Badalato
 * @author Sebastian Gadzinski
 * @author Chase Fridgen
 * @author Paul Roode
 * @version Iteration 5
 */
public class ElevatorSubsystem extends AbstractSubsystem {

    private final ElevatorStateMachine stateMachine;
    public HashMap<Integer, Boolean> lamps;
    public int elevatorNumber;
    private final CreateFile file;
    private int numRequests;
    private boolean printingEnabled, hardFault = false;
    private final UDPInfo schedulerUDPInfo;

    public ElevatorSubsystem(UDPInfo elevatorUDPInfo, int elevatorNumber, UDPInfo schedulerUDPInfo) {
        super(elevatorUDPInfo);
        stateMachine = new ElevatorStateMachine(
                ElevatorState.IDLE,
                ElevatorDoorStatus.CLOSED,
                ElevatorDirection.IDLE,
                0,
                new HashMap<>()
        );
        this.elevatorNumber = elevatorNumber;
        file = new CreateFile("Elevator" + elevatorNumber + ".txt");
        numRequests = 0;
        if (FAULT_PRINTING) {
            printingEnabled = false;
        }
        this.schedulerUDPInfo = schedulerUDPInfo;
    }

    /**
     * Sends the received data back to the Scheduler.
     *
     * @param response The data to send back to the Scheduler.
     */
    public synchronized void sendResponse(Request response) {
        sendRequest(response, schedulerUDPInfo.getInetAddress(), schedulerUDPInfo.getInSocketPort());
        printToFile("TimeStamp: " + getTimestamp() + "\nElevator #" + elevatorNumber + " sent: \n" + response);
    }

    /**
     * Wait until the queue receives a packet where this thread will be notified,
     * wake up, and then parse the packet
     *
     * @return the received packet
     */
    public synchronized Request fetchRequest() {
        return waitForRequest();
    }

    /**
     * Identifies request and sends to proper handler
     *
     * @param request received packet
     */
    public synchronized void handleRequest(Request request) {

        this.numRequests++;
        this.createFault(request);

        Request response = null;
        String responseString = "TimeStamp: " + getTimestamp() + "\nElevator #" + elevatorNumber + " received: \n";
        if (request instanceof ElevatorEmergencyRequest) {
            ElevatorEmergencyRequest emergencyRequest = (ElevatorEmergencyRequest) request;
            printToFile(responseString + emergencyRequest);
            if (emergencyRequest.getEmergencyState() == ElevatorEmergency.FIX) {
                response = stateMachine.handleRequest(emergencyRequest);
                fixSystem();
            } else {
                ElevatorEmergencyRequest terminationRequest = new ElevatorEmergencyRequest(getSource(), ElevatorEmergency.SHUTDOWN, ElevatorEmergencyRequest.COMPLETED_EMERGENCY, null, null);
                sendResponse(terminationRequest);
            }
        } else if (request instanceof FileRequest) {
            FileRequest fileRequest = (FileRequest) request;
            printToFile(responseString + fileRequest);

            response = handleFileRequest(fileRequest);
        } else if (request instanceof ElevatorDestinationRequest) {
            ElevatorDestinationRequest destinationRequest = (ElevatorDestinationRequest) request;
            // Turn on the lamp for the elevator button
            printToFile(responseString + destinationRequest);
            setLampStatus(destinationRequest.getRequestedDestinationFloor(), true);
            response = request;
        } else if (request instanceof ElevatorDoorRequest) {
            ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
            printToFile(responseString + doorRequest);

            response = stateMachine.handleRequest(doorRequest);

        } else if (request instanceof ElevatorMotorRequest) {
            ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
            if(hardFault && motorRequest.getRequestedDirection() != ElevatorDirection.IDLE) {
            	try {
					Thread.sleep(Config.TIMER_TIMEOUT + 5000);
					hardFault = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
            printToFile(responseString + motorRequest);

            response = stateMachine.handleRequest(motorRequest);
        } else if (request instanceof ElevatorPassengerWaitRequest) {
            ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
            printToFile(responseString + waitRequest);

            response = stateMachine.handleRequest(waitRequest);
        }
        if (response != null) {
            response.setSource(getSource());
            sendResponse(response);
            //Send the emergency request back is the end of a fault cycle
            if (response instanceof ElevatorEmergencyRequest && FAULT_PRINTING) {
                printingEnabled = false;
            }
        }
    }

    /**
     * Creates a fault for elevator subsystem
     */
    public void createFault(Request request) {
        if (request.getFault() > 0) {
            if (request instanceof FileRequest) return;
            printingEnabled = true;
            printToFile("FAULT IS: " + request.getFault());
            if (request.getFault() == 1) {
                this.makeDoorFault();
            } else if (request.getFault() == 2) {
                this.makeMotorFault();
            } else if (request.getFault() == 3) {
            	hardFault = true;
            }
        }
    }

    /**
     * Handles file request, sends a destination request from data in file request
     *
     * @param request The request to be dealt with.
     */
    public Request handleFileRequest(FileRequest request) {

        return new ElevatorDestinationRequest(
                new SubsystemSource(ELEVATOR_SUBSYSTEM, Integer.toString(elevatorNumber)),
                request.getOriginFloor(), request.getDirection());
    }

    /**
     * Get subsystem identification
     *
     * @return this subsystems identification
     */
    public SubsystemSource getSource() {
        return new SubsystemSource(ELEVATOR_SUBSYSTEM, Integer.toString(elevatorNumber));
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
     * "Fixes ElevatorSubsystem" -> sleeps thread for a certain fix time
     */
    public void fixSystem() {
        try {
            Thread.sleep(FIX_ELEVATOR_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a door fault for testing purposes
     */
    public void makeDoorFault() {
        this.stateMachine.setDoorFault();
    }

    /**
     * Creates a motor fault for testing purposes
     */
    public void makeMotorFault() {
        this.stateMachine.setMotorFault();
    }

    private void printToFile(String message) {
        if (printingEnabled)
            file.writeToFile("\n" + message);
    }

    /**
     * Attempts to fetch a packet. When this gets fetched, sends the response to the
     * scheduler.
     */
    @Override
    public void run() {
        printToFile("ElevatorSubsystem: " + elevatorNumber + " operational...\n");
        while (true) {
            Request fetchedRequest = fetchRequest();
            handleRequest(fetchedRequest);
        }
    }

    /**
     * Initializes and starts the ElevatorSubsystem threads.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        for (int elevatorNum = 0; elevatorNum < NUMBER_OF_ELEVATORS; ++elevatorNum) {
            ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(ELEVATORS_UDP_INFO[elevatorNum], elevatorNum, SCHEDULER_UDP_INFO);
            Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "ElevatorSubsystem " + elevatorNum);
            elevatorSubsystemThread.start();
        }
    }

}
