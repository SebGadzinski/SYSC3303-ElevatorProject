package project.tests.unit;

import org.junit.jupiter.api.Test;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.tests.stubs.FloorSubsystemStub;
import project.utils.datastructs.FileRequest;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static project.Config.*;

public class TestFloorSubsystem {

    @Test
    public void testFloorSubsystem() {

        FloorSubsystem floorSubsystem = new FloorSubsystem(localhost, getPort(), getPort(), 0);
        FloorSubsystemStub floorSubsystemStub = new FloorSubsystemStub();

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floorSubsystem.getSource());
                
        byte[] tmp = floorSubsystemStub.sendRequest(fileRequest);

        assertTrue(floorSubsystemStub.receiveAndAcknowledge(tmp));

    }

}
