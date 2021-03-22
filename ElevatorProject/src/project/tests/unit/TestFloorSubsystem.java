package project.tests.unit;

import org.junit.jupiter.api.Test;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.tests.stubs.FloorSubsystemStub;
import project.utils.datastructs.FileRequest;

import static project.Config.FLOORS_UDP_INFO;

public class TestFloorSubsystem {

    @Test
    public void testFloorSubsystem() {

        FloorSubsystem floorSubsystem = new FloorSubsystem(FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);
        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floorSubsystem.getSource());
        FloorSubsystemStub floorSubsystemStub = new FloorSubsystemStub();

        byte[] tmp = floorSubsystemStub.sendRequest(fileRequest);
        assert floorSubsystemStub.receiveAndAcknowledge(tmp);

    }

}
