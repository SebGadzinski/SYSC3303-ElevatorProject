package project.tests;

import org.junit.jupiter.api.Test;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.utils.datastructs.FileRequest;

class SchedulerStub_JUNIT {

    @Test
    void test() {
        FloorSubsystem floor = new FloorSubsystem(Config.FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);

        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floor.getSource());
        SchedulerStub test = new SchedulerStub(Config.FLOORS_UDP_INFO[0].getInetAddress(), 6969, 7070);
        int sendSuccess = 0;

        sendSuccess += test.sendRequestPub(fileRequest, Config.SCHEDULER_UDP_INFO.getInetAddress(), Config.SCHEDULER_UDP_INFO.getInSocketPort());

        System.out.println("Scheduler Stub sending a fileRequest");
        assert (sendSuccess == 1);
    }

}
