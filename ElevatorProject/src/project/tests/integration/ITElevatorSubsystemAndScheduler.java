package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.Config.getPort;
import static project.Config.localhost;
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;

import org.junit.jupiter.api.Test;

import project.systems.Scheduler;
import project.tests.stubs.ElevatorSubsystemStub;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.UDPInfo;

/**
 * Comprises integration tests for ElevatorSubsystem and Scheduler.
 */
public class ITElevatorSubsystemAndScheduler {

    /**
     * Tests communication between ElevatorSubsystem and Scheduler.
     */
    @Test
    public void testElevatorSubsystemAndScheduler() {

        UDPInfo schedulerUDPInfo = new UDPInfo(localhost, getPort(), getPort());
        UDPInfo elevator0UDPInfo = new UDPInfo(localhost, getPort(), getPort());

        Scheduler scheduler = new Scheduler(
                schedulerUDPInfo,
                new UDPInfo[]{elevator0UDPInfo},
                new UDPInfo[]{}
        );
        ElevatorSubsystemStub elevatorSubsystemStub = new ElevatorSubsystemStub(elevator0UDPInfo, 0, schedulerUDPInfo);

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, UP, 3, scheduler.getSource());

        int numSendSuccesses = 0;
        int numSendSuccessesTarget = 5;
        for (int i = 0; i < numSendSuccessesTarget; ++i) {
            numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, schedulerUDPInfo.getInetAddress(), schedulerUDPInfo.getInSocketPort());
        }

        assertEquals(numSendSuccessesTarget, numSendSuccesses);

    }

}