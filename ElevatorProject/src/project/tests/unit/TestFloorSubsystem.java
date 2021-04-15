package project.tests.unit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static project.Config.*;
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;

import org.junit.jupiter.api.Test;

import project.systems.FloorSubsystem;
import project.tests.stubs.FloorSubsystemStub;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.UDPInfo;

/**
 * Comprises unit tests for FloorSubsystem.
 */
public class TestFloorSubsystem {

    /**
     * Tests the capability of FloorSubsystem to send, receive, and confirm requests.
     */
    @Test
    public void testFloorSubsystem() {

        FloorSubsystem floorSubsystem = new FloorSubsystem(
                new UDPInfo(localhost, getTestPort(), getTestPort()),
                0,
                new UDPInfo(localhost, getTestPort(), getTestPort()),
                REQUEST_BATCH_FILENAME
        );
        FloorSubsystemStub floorSubsystemStub = new FloorSubsystemStub();

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, UP, 3, floorSubsystem.getSource());

        byte[] tmp = floorSubsystemStub.sendRequest(fileRequest);

        assertTrue(floorSubsystemStub.receiveAndAcknowledge(tmp));

    }

}
