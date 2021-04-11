package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.Config.REQUEST_BATCH_FILENAME;
import static project.Config.getPort;
import static project.Config.localhost;
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;

import org.junit.jupiter.api.Test;

import project.systems.FloorSubsystem;
import project.tests.stubs.SchedulerStub;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.UDPInfo;

/**
 * Comprises integration tests for FloorSubsystem and Scheduler.
 */
public class ITFloorSubsystemAndScheduler {

    /**
     * Tests communication between FloorSubsystem and Scheduler.
     */
    @Test
    public void testFloorSubsystemAndScheduler() {

        UDPInfo floor0UDPInfo = new UDPInfo(localhost, getPort(), getPort());
        UDPInfo schedulerUDPInfo = new UDPInfo(localhost, getPort(), getPort());

        FloorSubsystem floorSubsystem = new FloorSubsystem(floor0UDPInfo, 0, schedulerUDPInfo, REQUEST_BATCH_FILENAME);
        SchedulerStub schedulerStub = new SchedulerStub(schedulerUDPInfo, new UDPInfo[]{}, new UDPInfo[]{floor0UDPInfo});

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, UP, 3, floorSubsystem.getSource());

        int numSendSuccesses = 0;
        int numSendSuccessesTarget = 5;
        for (int i = 0; i < numSendSuccessesTarget; ++i) {
            numSendSuccesses += schedulerStub.sendRequestPub(fileRequest, floor0UDPInfo.getInetAddress(), floor0UDPInfo.getInSocketPort());
        }

        assertEquals(numSendSuccessesTarget, numSendSuccesses);

    }

}