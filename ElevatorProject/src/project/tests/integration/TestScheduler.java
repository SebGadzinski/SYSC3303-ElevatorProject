package project.tests.integration;

import org.junit.jupiter.api.Test;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.tests.stubs.SchedulerStub;
import project.utils.datastructs.FileRequest;

import static project.Config.FLOORS_UDP_INFO;
import static project.Config.SCHEDULER_UDP_INFO;

public class TestScheduler {

    //@Test
    public void testScheduler() {

        FloorSubsystem floorSubsystem = new FloorSubsystem(FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);
        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floorSubsystem.getSource());
        SchedulerStub schedulerStub = new SchedulerStub(FLOORS_UDP_INFO[0].getInetAddress(), 6969, 7070);

        int numSendSuccesses = 0;
        numSendSuccesses += schedulerStub.sendRequestPub(fileRequest, SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort());

        assert numSendSuccesses == 1;

    }

}
