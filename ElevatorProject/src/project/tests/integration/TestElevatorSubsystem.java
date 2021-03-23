package project.tests.integration;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.Scheduler;
import project.tests.stubs.ElevatorSubsystemStub;
import project.utils.datastructs.FileRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.Config.*;

import org.junit.jupiter.api.Test;

public class TestElevatorSubsystem {

    @Test
    public void testElevatorSubsystem() {

        Scheduler scheduler = new Scheduler(localhost, getPort(), getPort());
        ElevatorSubsystemStub elevatorSubsystemStub = new ElevatorSubsystemStub(localhost, getPort(), getPort());

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, scheduler.getSource());

        int numSendSuccesses = 0;
        numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, localhost, getPort());
        numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, localhost, getPort());
        numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, localhost, getPort());

        assertEquals(3, numSendSuccesses);

    }

}
