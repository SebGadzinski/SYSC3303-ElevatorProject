package project.models;

import project.systems.*;
import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Info:
 * Communication between Floor and Elevator
 * This is a client to the Scheduler
 * Sending:
 * Reply to elevator when work is to be done
 * Send data from Elevator to Floor
 * Receiving:
 * Input from Floor
 * Calls from Elevator
 */
public class Scheduler implements Runnable {

    // declare queue fields etc.
    public FloorSubsystem floorSubsystem;
    public ElevatorSubsystem elevatorSubSystem;
    //private static State state;

    /* <!-- not sure if needed for iter1 -Paul
    public enum State {
        WAITING_FOR_REQUEST
        // etc.
    }
    */

    public Scheduler(BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromElevatorSubsystem,
                     BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToElevatorSubsystem,
                     BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsFromFloorSubsystem,
                     BlockingQueue<ConcurrentMap<Request.Key, Object>> requestsToFloorSubsystem,
                     ElevatorSubsystem elevatorSubsystem, FloorSubsystem floorSubsystem) {

        // init queue fields etc.
        //state = State.WAITING_FOR_REQUEST;

    }

    /**
     * Gets the state of this Scheduler.
     *
     * @return The state of this Scheduler.
     */
    //public static State getState() {
    //    return state;
    //}

    @Override
    public void run() {

    }

}
