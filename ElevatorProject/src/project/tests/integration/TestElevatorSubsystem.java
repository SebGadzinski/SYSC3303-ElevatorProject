package project.tests.integration;

import static project.Config.ELEVATORS_UDP_INFO;
import static project.Config.FLOORS_UDP_INFO;
import static project.Config.SCHEDULER_UDP_INFO;

import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.Scheduler;
import project.tests.stubs.ElevatorSubsystemStub;
import project.utils.datastructs.FileRequest;

public class TestElevatorSubsystem {

	// @Test
	public void testElevatorSubsystem() {

		Scheduler scheduler = new Scheduler(SCHEDULER_UDP_INFO.getInetAddress(), SCHEDULER_UDP_INFO.getInSocketPort(),
				SCHEDULER_UDP_INFO.getOutSocketPort());
		ElevatorSubsystemStub elevatorSubsystemStub = new ElevatorSubsystemStub(FLOORS_UDP_INFO[0].getInetAddress(),
				6969, 7070);
		FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, scheduler.getSource(), 0);

		int numSendSuccesses = 0;
		numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, ELEVATORS_UDP_INFO[0].getInetAddress(),
				ELEVATORS_UDP_INFO[0].getInSocketPort());
		numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, ELEVATORS_UDP_INFO[1].getInetAddress(),
				ELEVATORS_UDP_INFO[1].getInSocketPort());
		numSendSuccesses += elevatorSubsystemStub.sendRequestPub(fileRequest, ELEVATORS_UDP_INFO[2].getInetAddress(),
				ELEVATORS_UDP_INFO[2].getInSocketPort());

		assert numSendSuccesses == 3;

	}

}
