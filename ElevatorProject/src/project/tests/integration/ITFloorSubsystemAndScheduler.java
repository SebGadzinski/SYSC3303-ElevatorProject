package project.tests.integration;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.tests.stubs.SchedulerStub;
import project.utils.datastructs.FileRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.Config.*;

import org.junit.jupiter.api.Test;

public class ITFloorSubsystemAndScheduler {

    @Test
    public void testScheduler() {

        FloorSubsystem floorSubsystem = new FloorSubsystem(localhost, getPort(), getPort(), 0);
        SchedulerStub schedulerStub = new SchedulerStub(localhost, getPort(), getPort());

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floorSubsystem.getSource());

        int numSendSuccesses = 0;
        numSendSuccesses += schedulerStub.sendRequestPub(fileRequest, localhost, getPort());

        assertEquals(1, numSendSuccesses);

    }

}
