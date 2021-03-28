package project.tests.unit;

import static org.junit.jupiter.api.Assertions.*;
import static project.Config.SCHEDULER_UDP_INFO;

import org.junit.jupiter.api.Test;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.ElevatorSubsystem;
import project.systems.Scheduler;
import project.utils.datastructs.ElevatorTimerWorker;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.SubsystemSource;
import project.utils.datastructs.SubsystemSource.Subsystem;

/**
 * 
 * @author Chase Badalato
 * @version 4
 * 
 * 
 * THIS TEST IS NOT USED FOR THIS ITERATION (TESTS ARE COMMENTED OUT)
 * 
 * PLEASE DISREGARD THESE TESTS
 * 
 * This tests the timer that is used within the scheduler to 
 * konw when a major elevator fault has occured.
 *
 */
class TestFaultsTimer {
	
	Scheduler scheduler;
	ElevatorSubsystem elevator;
	int outgoing;
	int incoming;
	
	public TestFaultsTimer() {
		int outgoingScheduler = Config.getPort();		
		int incomingScheduler = Config.getPort();
		int outgoingElevator = Config.getPort();
		int incomingElevator = Config.getPort();
		
        this.scheduler = new Scheduler(
                SCHEDULER_UDP_INFO.getInetAddress(),
                incomingScheduler,
                outgoingScheduler
        );	
		ElevatorSubsystem elevator = new ElevatorSubsystem(Config.ELEVATORS_UDP_INFO[0].getInetAddress(),
				incomingElevator, outgoingElevator, 0);
		Thread elevatorSubsystemThread = new Thread(elevator, "elevator#0");
		elevatorSubsystemThread.start();
        
        
	}
	
	/**
	 * Test a successful exchange of packets between an elevator subsystem
	 * and the sheduler. 
	 */
	//@Test
	public void testNoTimeout() {
		//this.scheduler.consumeRequest(new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, new SubsystemSource(Subsystem.FLOOR_SUBSYSTEM, "0")));
		assertEquals(4, 4);
	}
	
	/**
	 * Test a failed exchange of packets between an elevator subsystem
	 * and the scheduler
	 */
	//@Test
	public void testTimeout() {
		assertEquals(3, 4);		
	}
	
}
