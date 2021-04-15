package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static project.Config.*;

import org.junit.jupiter.api.Test;

import project.systems.ElevatorSubsystem;
import project.systems.Scheduler;
import project.tests.stubs.FloorSubsystemStub2;
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

        UDPInfo schedulerUDPInfo = new UDPInfo(localhost, getTestPort(), getTestPort());

        FloorSubsystemStub2 floors[] = new FloorSubsystemStub2[NUMBER_OF_FLOORS];
        ElevatorSubsystem elevators[] = new ElevatorSubsystem[NUMBER_OF_ELEVATORS];

        UDPInfo[] floorArray = new UDPInfo[NUMBER_OF_FLOORS];
        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            floorArray[i] = new UDPInfo(localhost, getTestPort(), getTestPort());
            floors[i] = new FloorSubsystemStub2(floorArray[i], i, schedulerUDPInfo, REQUEST_BATCH_FILENAME);
        }

        UDPInfo[] elevatorArray = new UDPInfo[NUMBER_OF_ELEVATORS];
        for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
            elevatorArray[i] = new UDPInfo(localhost, getTestPort(), getTestPort());
            elevators[i] = new ElevatorSubsystem(elevatorArray[i], i, schedulerUDPInfo);
        }

        Scheduler scheduler = new Scheduler(schedulerUDPInfo, elevatorArray, floorArray);
        Thread schedulerThread = new Thread(scheduler, "scheduler");
        schedulerThread.start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < NUMBER_OF_FLOORS; i++) {
            Thread floorThread = new Thread(floors[i], "floor");
            floorThread.start();
        }

        for (int i = 0; i < NUMBER_OF_ELEVATORS; i++) {
            Thread elevatorThread = new Thread(elevators[i], "elevator");
            elevatorThread.start();
        }
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertEquals(floors[0].receivedArrival(), 1);
    }

}
