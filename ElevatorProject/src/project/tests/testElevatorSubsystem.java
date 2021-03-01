package project.tests;

import static org.junit.jupiter.api.Assertions.*;
import static project.Config.REQUEST_QUEUE_CAPACITY;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.junit.jupiter.api.Test;

import project.models.Scheduler;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.FileRequest;

class testElevatorSubsystem  {

    BlockingQueue<Request> requestsFromElevatorSubsystem;
    BlockingQueue<Request> requestsToElevatorSubsystem;
    BlockingQueue<Request> requestsFromFloorSubsystem;
    BlockingQueue<Request> requestsToFloorSubsystem;

    // initialize active components
    ElevatorSubsystem elevatorSubsystem;
    FloorSubsystem floorSubsystem;
    Scheduler scheduler;
    
	
	@Test
	void testFetch() throws FileNotFoundException {
		
        BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToSchedular    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
        // initialize active components
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsToSchedular, new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, 0,
                new HashMap<Integer, Boolean>()));
        FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsToSchedular);
        Scheduler scheduler                 = new Scheduler(requestsToSchedular, requestsToElevatorSubsystem, requestsToFloorSubsystem);
	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
        Request fetchedRequest = scheduler.fetchRequest();
        
        scheduler.dispatchRequestToElevatorSubsystem(fetchedRequest);
        fetchedRequest = elevatorSubsystem.fetchRequest();
        
        if(fetchedRequest instanceof FileRequest) {
    		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
    		assertEquals(fileRequest.getTime(), "18:17:17.020");
            assertEquals(fileRequest.getOriginFloor(), 1);
            assertEquals(fileRequest.getDestinationFloor(), 3);
        }
        
	}

	@Test
	void testSend() throws FileNotFoundException {
		
        BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToSchedular    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
        // initialize active components
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsToSchedular, new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, 0,
                new HashMap<Integer, Boolean>()));
        FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsToSchedular);
        Scheduler scheduler                 = new Scheduler(requestsToSchedular, requestsToElevatorSubsystem, requestsToFloorSubsystem);
	    
        System.out.println("Start");
        System.out.println("HERE 0");
        ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
        Request fetchedRequest = scheduler.fetchRequest();
        
        scheduler.dispatchRequestToElevatorSubsystem(fetchedRequest);

        fetchedRequest = elevatorSubsystem.fetchRequest();

        elevatorSubsystem.sendResponse(fetchedRequest);

        scheduler.fetchRequest();

        
        if(fetchedRequest instanceof FileRequest) {
    		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
    		assertEquals(fileRequest.getTime(), "18:17:17.020");
            assertEquals(fileRequest.getOriginFloor(), 1);
            assertEquals(fileRequest.getDestinationFloor(), 3);
    		
        }
        
	}
}
