package project.tests;

import static project.Config.SCHEDULER_UDP_INFO;

import org.junit.jupiter.api.Test;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.Scheduler;
import project.utils.datastructs.FileRequest;

class ElevatorSubsystemStub_JUNIT {

    @Test
    void test() {
        Scheduler scheduler = new Scheduler(SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort(),
                SCHEDULER_UDP_INFO.getOutSocketPort());

        ElevatorSubsystemStub_2 test = new ElevatorSubsystemStub_2(Config.FLOORS_UDP_INFO[0].getInetAddress(), 6969, 7070);
        FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, scheduler.getSource());
        int sendSuccess = 0;

        sendSuccess += test.sendRequestPub(fileRequest, Config.ELEVATORS_UDP_INFO[0].getInetAddress(), Config.ELEVATORS_UDP_INFO[0].getInSocketPort());
        sendSuccess += test.sendRequestPub(fileRequest, Config.ELEVATORS_UDP_INFO[1].getInetAddress(), Config.ELEVATORS_UDP_INFO[1].getInSocketPort());
        sendSuccess += test.sendRequestPub(fileRequest, Config.ELEVATORS_UDP_INFO[2].getInetAddress(), Config.ELEVATORS_UDP_INFO[2].getInSocketPort());

        System.out.println("Elevator Stub sending a fileRequest");
        assert (sendSuccess == 3);
    }

}
