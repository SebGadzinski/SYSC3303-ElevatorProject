package project.systems;

import static project.Config.SCHEDULER_UDP_INFO;

import java.net.InetAddress;
import java.util.HashMap;

import project.Config;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.utils.datastructs.ElevatorDestinationRequest;
import project.utils.datastructs.ElevatorDoorRequest;
import project.utils.datastructs.ElevatorEmergencyRequest;
import project.utils.datastructs.ElevatorEmergencyRequest.ElevatorEmergency;
import project.utils.datastructs.ElevatorMotorRequest;
import project.utils.datastructs.ElevatorPassengerWaitRequest;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.SubsystemSource;
import project.utils.datastructs.SubsystemSource.Subsystem;
import project.utils.objects.general.CreateFile;

/**
 * Receives data from the scheduler and then sends it right back
 *
 * @author Iter1 (Chase Badalato), Iter2 (Sebastian Gadzinski), Iter3 (Sebastian
 *         Gadzinski)
 */
public class ElevatorSubsystem extends AbstractSubsystem implements Runnable {

	private final ElevatorStateMachine stateMachine;
	public HashMap<Integer, Boolean> lamps;
	public int elevatorNumber;
	private final CreateFile file;
	private int numOfRequests;

	public ElevatorSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort, int elevatorNumber) {
		super(inetAddress, inSocketPort, outSocketPort);
		this.stateMachine = new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED,
				ElevatorDirection.IDLE, 0, new HashMap<>());
		this.elevatorNumber = elevatorNumber;
		file = new CreateFile("Elevator" + elevatorNumber + ".txt");
		this.numOfRequests = 0;
	}

	/**
	 * send the received data back to the scheduler
	 *
	 * @param response the data to send to the scheduler
	 */
	public synchronized void sendResponse(Request response) {
		sendRequest(response, SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());
		file.writeToFile(getSource() + "\nResponded to Scheduler: \n " + response);
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

		this.numOfRequests++;
		this.sendFault();

		Request response = null;
		String responseString = "\nRequest received by:\n" + getSource() + "\n";
		if (request instanceof ElevatorEmergencyRequest) {
			ElevatorEmergencyRequest emergencyRequest = (ElevatorEmergencyRequest) request;
			file.writeToFile(responseString + emergencyRequest);
			if (emergencyRequest.getEmergencyState() == ElevatorEmergency.FIX) {
				response = stateMachine.handleRequest(emergencyRequest);
				fixSystem();
			} else {
				System.exit(-1);
			}
		} else if (request instanceof FileRequest) {
			FileRequest fileRequest = (FileRequest) request;
			file.writeToFile(responseString + fileRequest);

			response = handleFileRequest(fileRequest);
		} else if (request instanceof ElevatorDestinationRequest) {
			ElevatorDestinationRequest destinationRequest = (ElevatorDestinationRequest) request;
			// Turn on the lamp for the elevator button
			file.writeToFile(responseString + destinationRequest);
			setLampStatus(destinationRequest.getRequestedDestinationFloor(), true);
			response = request;
		} else if (request instanceof ElevatorDoorRequest) {
			ElevatorDoorRequest doorRequest = (ElevatorDoorRequest) request;
			file.writeToFile(responseString + doorRequest);

			response = stateMachine.handleRequest(doorRequest);

		} else if (request instanceof ElevatorMotorRequest) {
			ElevatorMotorRequest motorRequest = (ElevatorMotorRequest) request;
			file.writeToFile(responseString + motorRequest);

			response = stateMachine.handleRequest(motorRequest);
		} else if (request instanceof ElevatorPassengerWaitRequest) {
			ElevatorPassengerWaitRequest waitRequest = (ElevatorPassengerWaitRequest) request;
			file.writeToFile(responseString + waitRequest);

			response = stateMachine.handleRequest(waitRequest);
		}
		if (response != null) {
			response.setSource(getSource());
			sendResponse(response);
		}
	}

	public void sendFault() {
		for (int i = 0; i < Config.faults.length; i++) {
			if (Config.faults[i].numUntilFault == numOfRequests) {

				if (Config.faults[i].fault == 1) {
					this.makeDoorFault();
				} else if (Config.faults[i].fault == 2) {
					this.makeMotorFault();
				}
			}

			else {
				break;
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
				new SubsystemSource(Subsystem.ELEVATOR_SUBSYSTEM, Integer.toString(elevatorNumber)),
				request.getOriginFloor(), request.getDirection());
	}

	/**
	 * Get subsystem identification
	 *
	 * @return this subsystems identification
	 */
	public SubsystemSource getSource() {
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

	public void fixSystem() {
		try {
			Thread.sleep(Config.FIX_ELEVATOR_TIME);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void makeDoorFault() {
		this.stateMachine.setDoorFault();
	}

	public void makeMotorFault() {
		this.stateMachine.setMotorFault();
	}

	/**
	 * Attempts to fetch a packet. When this gets fetched, sends the response to the
	 * scheduler.
	 */
	@Override
	public void run() {
		file.writeToFile("ElevatorSubsystem: " + elevatorNumber + " operational...\n");
		while (true) {
			Request fetchedRequest = fetchRequest();
			handleRequest(fetchedRequest);
		}
	}

	public static void main(String[] args) {
		for (int i = 0; i < Config.NUMBER_OF_ELEVATORS; i++) {
			ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(Config.ELEVATORS_UDP_INFO[i].getInetAddress(),
					Config.ELEVATORS_UDP_INFO[i].getInSocketPort(), Config.ELEVATORS_UDP_INFO[i].getOutSocketPort(), i);
			Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "elevator#" + i);
			elevatorSubsystemThread.start();
		}
	}

}
