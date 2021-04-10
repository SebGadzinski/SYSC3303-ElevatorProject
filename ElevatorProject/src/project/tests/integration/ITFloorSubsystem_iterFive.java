package project.tests.integration;

import static org.junit.jupiter.api.Assertions.*;
import static project.Config.getPort;
import static project.Config.localhost;
import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;

import org.junit.jupiter.api.Test;

import project.Config;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.systems.Scheduler;
import project.tests.stubs.FloorSubsystemStub2;
import project.tests.stubs.SchedulerStub;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.UDPInfo;

class ITFloorSubsystem_iterFive {

	/**
	 * This test is mainly aimed at making sure that the FloorSubsystem is functioning
	 * Only a single request is made for all of the floors and then it is made sure that 
	 * the respective floor (in this case 1) gets a confirmation packet that the passenger
	 * had arrived
	 */
	@Test
	void test() {
	
        UDPInfo schedulerUDPInfo = new UDPInfo(localhost, getPort(), getPort());
        
        FloorSubsystemStub2 floors[] = new FloorSubsystemStub2[Config.NUMBER_OF_FLOORS];
        ElevatorSubsystem elevators[] = new ElevatorSubsystem[Config.NUMBER_OF_ELEVATORS];
        
        UDPInfo[] floorArray = new UDPInfo[Config.NUMBER_OF_FLOORS];
        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i ++) {
        	floorArray[i] = new UDPInfo(localhost, getPort(), getPort()); 
        	floors[i] = new FloorSubsystemStub2(floorArray[i], i, schedulerUDPInfo);
        }
        
        UDPInfo[] elevatorArray = new UDPInfo[Config.NUMBER_OF_ELEVATORS];
        for (int i = 0; i < Config.NUMBER_OF_ELEVATORS; i ++) {
        	elevatorArray[i] = new UDPInfo(localhost, getPort(), getPort()); 
        	elevators[i] = new ElevatorSubsystem(elevatorArray[i], i, schedulerUDPInfo);
        }
        
        Scheduler scheduler = new Scheduler(schedulerUDPInfo, elevatorArray, floorArray);
        Thread schedulerThread = new Thread(scheduler, "scheduler");
        schedulerThread.start();
        try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        for (int i = 0; i < Config.NUMBER_OF_FLOORS; i ++) {
            Thread floorThread = new Thread(floors[i], "floor");     
            floorThread.start();
        }
        
        for (int i = 0; i < Config.NUMBER_OF_ELEVATORS; i ++) {
            Thread elevatorThread = new Thread(elevators[i], "elevator");     
            elevatorThread.start();
        }       
    	try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        assertEquals(floors[0].receivedArrival(), 1);     
	}

}
