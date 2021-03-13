// package project.tests;

// import static org.junit.jupiter.api.Assertions.*;
// import static project.Config.REQUEST_QUEUE_CAPACITY;

// import java.io.FileNotFoundException;
// import java.util.HashMap;
// import java.util.concurrent.ArrayBlockingQueue;
// import java.util.concurrent.BlockingQueue;

// import org.junit.jupiter.api.Test;

// import project.systems.Scheduler;
// import project.state_machines.ElevatorStateMachine;
// import project.state_machines.ElevatorStateMachine.ElevatorDirection;
// import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
// import project.state_machines.ElevatorStateMachine.ElevatorState;
// import project.systems.ElevatorSubsystem;
// import project.systems.FloorSubsystem;
// import project.utils.datastructs.ReadRequestResult;
// import project.utils.datastructs.Request;
// import project.utils.datastructs.FileRequest;

// class testScheduler  {

//     BlockingQueue<Request> requestsFromElevatorSubsystem;
//     BlockingQueue<Request> requestsToElevatorSubsystem;
//     BlockingQueue<Request> requestsFromFloorSubsystem;
//     BlockingQueue<Request> requestsToFloorSubsystem;

//     // initialize active components
//     ElevatorSubsystem elevatorSubsystem;
//     FloorSubsystem floorSubsystem;
//     Scheduler scheduler;
    
	
// 	@Test
// 	void testFetchFloor() throws FileNotFoundException {
		
//         BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
//         BlockingQueue<Request> requestsToSchedular    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
//         BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
//         // initialize active components      
//         FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsToSchedular);
//         Scheduler scheduler                 = new Scheduler(requestsToSchedular, requestsToElevatorSubsystem, requestsToFloorSubsystem);
        
//         ReadRequestResult readRequestResult = floorSubsystem.readRequest();
// 	    System.out.println("Read Request");
// 	    floorSubsystem.sendRequest(readRequestResult.getRequest());
// 	    System.out.println("Sent Request");
//         Request fetchedRequest = scheduler.fetchRequest();
//         System.out.println("Picked From Schedular");
                
//         if(fetchedRequest instanceof FileRequest) {
//     		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
//     		assertEquals(fileRequest.getTime(), "23:13:17.020");
//             assertEquals(fileRequest.getOriginFloor(), 1);
//             assertEquals(fileRequest.getDestinationFloor(), 6);
//         }
// 	}

// 	@Test
// 	void testFetchElevator() throws FileNotFoundException {
		
//         BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
//         BlockingQueue<Request> requestsToSchedular    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
//         BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
//         // initialize active components
//         ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsToSchedular, new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, 0,
//         new HashMap<Integer, Boolean>()));        
//         FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsToSchedular);
//         Scheduler scheduler                 = new Scheduler(requestsToSchedular, requestsToElevatorSubsystem, requestsToFloorSubsystem);
// 	    ReadRequestResult readRequestResult = floorSubsystem.readRequest();
// 	    floorSubsystem.sendRequest(readRequestResult.getRequest());
	    
//         Request fetchedRequest = scheduler.fetchRequest();
        
//         scheduler.dispatchRequestToElevatorSubsystem(fetchedRequest);
//         fetchedRequest = elevatorSubsystem.fetchRequest();
//         elevatorSubsystem.sendResponse(fetchedRequest);
//         scheduler.fetchRequest();
        
//         if(fetchedRequest instanceof FileRequest) {
//     		FileRequest fileRequest = (FileRequest) fetchedRequest;
    		
//     		assertEquals(fileRequest.getTime(), "23:13:17.020");
//             assertEquals(fileRequest.getOriginFloor(), 1);
//             assertEquals(fileRequest.getDestinationFloor(), 6);
    		
//         }
// 	}
// }
