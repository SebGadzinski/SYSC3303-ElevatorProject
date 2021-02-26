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
        BlockingQueue<Request> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToSchedular    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<Request> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
        // initialize active components
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsToSchedular, new ElevatorStateMachine(ElevatorState.IDLE, ElevatorDoorStatus.CLOSED, ElevatorDirection.IDLE, 0,
                new HashMap<Integer, Boolean>()));
        FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsToFloorSubsystem, requestsToSchedular);
        Scheduler scheduler                 = new Scheduler(requestsToSchedular, requestsToElevatorSubsystem, requestsToFloorSubsystem);
                
        // initialize threads
        Thread elevatorSubsystemThread = new Thread(elevatorSubsystem, "ElevatorSubsystem");
        Thread floorSubsystemThread    = new Thread(floorSubsystem, "FloorSubsystem");
        Thread schedulerThread         = new Thread(scheduler, "Scheduler");

        // start threads
        elevatorSubsystemThread.start();
        floorSubsystemThread.start();
        schedulerThread.start();

    }

}
