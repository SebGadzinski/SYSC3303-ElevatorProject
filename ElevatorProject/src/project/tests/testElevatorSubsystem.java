package project.tests;

import static org.junit.jupiter.api.Assertions.*;
import static project.Config.REQUEST_QUEUE_CAPACITY;

import java.io.FileNotFoundException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import org.junit.jupiter.api.Test;

import project.models.Scheduler;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

class testElevatorSubsystem  {

    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem;
    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem;
    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem;
    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem;

    // initialize active components
    ElevatorSubsystem elevatorSubsystem;
    FloorSubsystem floorSubsystem;
    Scheduler scheduler;
    
	
	@Test
	void testFetch() throws FileNotFoundException {
		
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	
	    // initialize active components
	    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsFromElevatorSubsystem);
	    FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsFromFloorSubsystem);
	    Scheduler scheduler                 = new Scheduler(requestsFromElevatorSubsystem, requestsToElevatorSubsystem,
	                                                        requestsFromFloorSubsystem, requestsToFloorSubsystem,
	                                                        elevatorSubsystem, floorSubsystem);
	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
        ConcurrentMap<Request.Key, Object> fetchedRequest = scheduler.fetchFromFloorSubsystemRequest();
        
        scheduler.sendRequestToElevatorSubsystem(fetchedRequest);
        fetchedRequest = elevatorSubsystem.fetchRequest();
        
        assertEquals(fetchedRequest.get(Request.Key.TIME), "23:13:17.020");
        assertEquals(fetchedRequest.get(Request.Key.ORIGIN_FLOOR), 1);
        assertEquals(fetchedRequest.get(Request.Key.DESTINATION_FLOOR), 6);
	}

	@Test
	void testSend() throws FileNotFoundException {
		
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	    BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
	
	    // initialize active components
	    ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsFromElevatorSubsystem);
	    FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsFromFloorSubsystem);
	    Scheduler scheduler                 = new Scheduler(requestsFromElevatorSubsystem, requestsToElevatorSubsystem,
	                                                        requestsFromFloorSubsystem, requestsToFloorSubsystem,
	                                                        elevatorSubsystem, floorSubsystem);
	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
        ConcurrentMap<Request.Key, Object> fetchedRequest = scheduler.fetchFromFloorSubsystemRequest();
        
        scheduler.sendRequestToElevatorSubsystem(fetchedRequest);
        fetchedRequest = elevatorSubsystem.fetchRequest();
        elevatorSubsystem.sendResponse(fetchedRequest);
        scheduler.fetchFromElevatorSubsystemRequest();
        
        assertEquals(fetchedRequest.get(Request.Key.TIME), "23:13:17.020");
        assertEquals(fetchedRequest.get(Request.Key.ORIGIN_FLOOR), 1);
        assertEquals(fetchedRequest.get(Request.Key.DESTINATION_FLOOR), 6);
        
	}
}
