package project.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;

import project.systems.FloorSubsystem;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;

/**
 * Tests FloorSubsytem.java
 *
 * @author Chase Fridgen
 * @author Sebastian Gadzinski
 */
class testFloorSubsystem {

	@Test
	void testReadRequests() throws FileNotFoundException {
		
		FloorSubsystem floorSubsystem = new FloorSubsystem(null, null);
	
        ReadRequestResult readRequestResult = floorSubsystem.readRequest();
        assertEquals(readRequestResult.getRequest().get(Request.Key.TIME), "23:13:17.020");
        assertEquals(readRequestResult.getRequest().get(Request.Key.ORIGIN_FLOOR), 1);
        assertEquals(readRequestResult.getRequest().get(Request.Key.DIRECTION), "up");
        assertEquals(readRequestResult.getRequest().get(Request.Key.DESTINATION_FLOOR), 6);
	}

}
