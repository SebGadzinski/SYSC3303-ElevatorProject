package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.tests.stubs.ElevatorSubsystemFaultStub;

public class TestElevatorSubsystemFault {

	@Test
	public void testWhenFault() {
		ElevatorState faultState = ElevatorState.FAULT_HANDLING;

		ElevatorSubsystemFaultStub esfs = new ElevatorSubsystemFaultStub();
		ElevatorState faultCheck = esfs.checkIfFault(0);

		assertEquals(faultCheck, faultState);
	}

	@Test
	public void testNoFault() {
		ElevatorState faultState = ElevatorState.FAULT_HANDLING;

		ElevatorSubsystemFaultStub esfs = new ElevatorSubsystemFaultStub();
		ElevatorState faultCheck = esfs.checkIfFault(1);

		assertNotEquals(faultCheck, faultState);
	}
}
