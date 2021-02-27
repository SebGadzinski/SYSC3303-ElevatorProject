package project;

import project.models.Scheduler;
import project.state_machines.ElevatorStateMachine;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.state_machines.ElevatorStateMachine.ElevatorDoorStatus;
import project.state_machines.ElevatorStateMachine.ElevatorState;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.Request;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static project.Config.REQUEST_QUEUE_CAPACITY;

/**
 * The driving class.
 *
 * @author Paul Roode
 */
public class Runner {

    public static void main(String[] args) {

        // initialize thread-safe request queues
        BlockingQueue<Request> requestsToFloorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToScheduler = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);

        // initialize active components
        FloorSubsystem floorSubsystem = new FloorSubsystem(requestsToFloorSubsystem, requestsToScheduler);
        Scheduler scheduler = new Scheduler(requestsToScheduler, requestsToElevatorSubsystem, requestsToFloorSubsystem);
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(
                requestsToElevatorSubsystem,
                requestsToScheduler,
                new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, 0, new HashMap<>())
        );

        // initialize threads
        Thread floorSubsystemThread = new Thread(floorSubsystem, "FloorSubsystem");
        Thread schedulerThread = new Thread(scheduler, "Scheduler");
        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "ElevatorSubsystem");

        // start threads
        floorSubsystemThread.start();
        schedulerThread.start();
        elevatorSubsystemThread.start();

    }

}
