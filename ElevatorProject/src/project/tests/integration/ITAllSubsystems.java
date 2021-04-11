package project.tests.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static project.Config.NUMBER_OF_ELEVATORS;
import static project.Config.NUMBER_OF_FLOORS;
import static project.Config.NUMBER_OF_REQUESTS;
import static project.Config.REQUEST_BATCH_FILENAME;
import static project.Config.getTestPort;
import static project.Config.localhost;

import java.time.Duration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.systems.Scheduler;
import project.utils.datastructs.UDPInfo;

/**
 * Comprises functional integration tests that spin up subsystem threads to verify that all of the subsystems can
 * coordinate on optimally servicing all inputted requests, including all of the different kinds of faults.
 *
 * @author Paul Roode
 * @version Iteration 5
 */
public class ITAllSubsystems {

    private static final Duration EXECUTION_THRESHOLD = Duration.ofMillis(NUMBER_OF_REQUESTS * 15000);
    private static FloorSubsystem[] floorSubsystems;
    private static Scheduler scheduler;

    /**
     * Spins up the required subsystem threads and initiates the servicing of requests.
     */
    @BeforeAll
    public static void setup() {

        // Initialize subsystem UDP info
        //---------------------------------------------------------------------------------------------------------
        UDPInfo schedulerUDPInfo = new UDPInfo(localhost, getTestPort(), getTestPort());

        UDPInfo[] elevatorsUDPInfo = new UDPInfo[NUMBER_OF_ELEVATORS];
        for (int elevatorNum = 0; elevatorNum < NUMBER_OF_ELEVATORS; ++elevatorNum) {
            elevatorsUDPInfo[elevatorNum] = new UDPInfo(localhost, getTestPort(), getTestPort());
        }

        UDPInfo[] floorsUDPInfo = new UDPInfo[NUMBER_OF_FLOORS];
        for (int floorNum = 0; floorNum < NUMBER_OF_FLOORS; ++floorNum) {
            floorsUDPInfo[floorNum] = new UDPInfo(localhost, getTestPort(), getTestPort());
        }
        //---------------------------------------------------------------------------------------------------------

        // Spin up the subsystem threads
        //---------------------------------------------------------------------------------------------------------
        scheduler = new Scheduler(schedulerUDPInfo, elevatorsUDPInfo, floorsUDPInfo);
        Thread schedulerThread = new Thread(scheduler, "TestScheduler");
        schedulerThread.start();

        for (int elevatorNum = 0; elevatorNum < NUMBER_OF_ELEVATORS; ++elevatorNum) {
            ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(
                    elevatorsUDPInfo[elevatorNum],
                    elevatorNum,
                    schedulerUDPInfo
            );
            Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "TestElevatorSubsystem " + elevatorNum);
            elevatorSubsystemThread.start();
        }

        try { // ensure the FloorSubsystem threads start last
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        floorSubsystems = new FloorSubsystem[NUMBER_OF_FLOORS];
        for (int floorNum = 0; floorNum < NUMBER_OF_FLOORS; ++floorNum) {
            FloorSubsystem floorSubsystem = new FloorSubsystem(
                    floorsUDPInfo[floorNum],
                    floorNum,
                    schedulerUDPInfo,
                    REQUEST_BATCH_FILENAME
            );
            floorSubsystems[floorNum] = floorSubsystem;
            Thread floorSubsystemThread = new Thread(floorSubsystem, "TestFloorSubsystem " + floorNum);
            floorSubsystemThread.start();
        }
        //---------------------------------------------------------------------------------------------------------

    }

    @Nested
    public class PostRequestServicing {

        /**
         * Validates the execution time of the whole system.
         */
        @Test
        @Order(0)
        public void testExecutionTime() {
            assertTimeout(EXECUTION_THRESHOLD, () -> {
                while (scheduler.isGUIRunning()) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        /**
         * Verifies the number of confirmed destination arrivals following the servicing of all requests.
         */
        @Test
        public void testRequestFulfillment() {
            int numArrivals = 0;
            for (int floorNum = 0; floorNum < NUMBER_OF_FLOORS; ++floorNum) {
                numArrivals += floorSubsystems[floorNum].getNumFileRequestsFulfilled();
            }
            assertEquals(NUMBER_OF_REQUESTS, numArrivals);
        }

    }

}