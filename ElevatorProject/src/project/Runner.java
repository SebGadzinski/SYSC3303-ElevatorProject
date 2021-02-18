package project;

import project.models.Floor;
import project.models.Scheduler;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.Request;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

import static project.Config.REQUEST_QUEUE_CAPACITY;

/**
 * The driving class.
 *
 * @author Paul Roode
 */
public class Runner {

    public static void main(String[] args) {

        // initialize thread-safe request queues
        BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem   = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem    = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem      = new ArrayBlockingQueue<>(REQUEST_QUEUE_CAPACITY);
        
        // initialize active components
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem(requestsToElevatorSubsystem, requestsFromElevatorSubsystem);
        FloorSubsystem floorSubsystem       = new FloorSubsystem(requestsFromFloorSubsystem, requestsToFloorSubsystem);
        Scheduler scheduler                 = new Scheduler(requestsFromElevatorSubsystem, requestsToElevatorSubsystem,
                                                            requestsFromFloorSubsystem, requestsToFloorSubsystem,
                                                            elevatorSubsystem, floorSubsystem);

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
