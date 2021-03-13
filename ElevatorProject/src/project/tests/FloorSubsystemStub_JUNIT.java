package project.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.utils.datastructs.FileRequest;

class FloorSubsystemStub_JUNIT {

	@Test
	void test() {
		System.out.println("Waiting for packet from a floor subsystem");
		
		FloorSubsystem floor = new FloorSubsystem(Config.FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);
 		FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floor.getSource());
		FloorSubsystemStub test = new FloorSubsystemStub();

		byte[] tmp = test.sendRequest(fileRequest);
		assert(test.receiveAndAcknowledge(tmp));
	}
}
