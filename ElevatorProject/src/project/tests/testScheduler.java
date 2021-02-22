package project.tests;

import static org.junit.jupiter.api.Assertions.*;
import static project.Config.REQUEST_QUEUE_CAPACITY;

import java.io.FileNotFoundException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;

import project.models.Scheduler;
import project.state_machines.ElevatorState;
import project.state_machines.ElevatorState.ElevatorDirection;
import project.state_machines.ElevatorState.ElevatorDoorStatus;
import project.state_machines.ElevatorState.ElevatorStateStatus;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.FileRequest;

class testScheduler  {

    BlockingQueue<Request> requestsFromElevatorSubsystem;
    BlockingQueue<Request> requestsToElevatorSubsystem;
    BlockingQueue<Request> requestsFromFloorSubsystem;
    BlockingQueue<Request> requestsToFloorSubsystem;

    // initialize active components
    ElevatorSubsystem elevatorSubsystem;
    FloorSubsystem floorSubsystem;
    Scheduler scheduler;
    
	
	@Test
	void testFetchFloor() throws FileNotFoundException {
		
	    BlockingQueue<Request> requestsFromElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsFromFloorSubsystem    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	
	    // initialize active components	    
	    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsFromElevatorSubsystem, new ElevatorState(ElevatorStateStatus.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE));
	    FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsFromFloorSubsystem);
	    Scheduler scheduler                 = new Scheduler(requestsFromElevatorSubsystem, requestsToElevatorSubsystem,
	                                                        requestsFromFloorSubsystem, requestsToFloorSubsystem,
	                                                        elevatorSubsystem, floorSubsystem);
	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    System.out.println("Read Request");
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    System.out.println("Sent Request");
        Request fetchedRequest = scheduler.fetchFromFloorSubsystemRequest();
        System.out.println("Picked From Schedular");
                
        if(fetchedRequest instanceof FileRequest) {
    		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
    		assertEquals(fileRequest.getTime(), "23:13:17.020");
            assertEquals(fileRequest.getOrginFloor(), 1);
            assertEquals(fileRequest.getDestinatinoFloor(), 6);
    		
        }
	}

	@Test
	void testFetchElevator() throws FileNotFoundException {
		
	    BlockingQueue<Request> requestsFromElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsFromFloorSubsystem    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	
	    // initialize active components
	    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsFromElevatorSubsystem, new ElevatorState(ElevatorStateStatus.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE));
	    FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsFromFloorSubsystem);
	    Scheduler scheduler                 = new Scheduler(requestsFromElevatorSubsystem, requestsToElevatorSubsystem,
	                                                        requestsFromFloorSubsystem, requestsToFloorSubsystem,
	                                                        elevatorSubsystem, floorSubsystem);
	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
        Request fetchedRequest = scheduler.fetchFromFloorSubsystemRequest();
        
        scheduler.sendRequestToElevatorSubsystem(fetchedRequest);
        fetchedRequest = elevatorSubsystem.fetchRequest();
        elevatorSubsystem.sendResponse(fetchedRequest);
        scheduler.fetchFromElevatorSubsystemRequest();
        
        if(fetchedRequest instanceof FileRequest) {
    		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
    		assertEquals(fileRequest.getTime(), "23:13:17.020");
            assertEquals(fileRequest.getOrginFloor(), 1);
            assertEquals(fileRequest.getDestinatinoFloor(), 6);
    		
        }
	}
}
