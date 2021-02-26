package project.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.utils.datastructs.Request;
import project.utils.datastructs.FileRequest;

/**
 * Tests FloorSubsytem.java
 *
 * @author Chase Fridgen
 * @author Sebastian Gadzinski
 */
class testFloorSubsystem {

	@Test
	void testReadRequests() throws FileNotFoundException {
		
		FloorSubsystem floorSubsystem = new FloorSubsystem();
	
        Request readRequestResult = floorSubsystem.readRequest().getRequest();
        
        if(readRequestResult instanceof FileRequest) {
    		FileRequest fileRequest = (FileRequest) readRequestResult;
    		
    		assertEquals(fileRequest.getTime(), "23:13:17.020");
            assertEquals(fileRequest.getOriginFloor(), 1);
            assertEquals(fileRequest.getDirection(), ElevatorDirection.UP);
            assertEquals(fileRequest.getDestinationFloor(), 6);
    		
        }
	}

}
