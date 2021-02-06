package project.systems;

import project.utils.datastructs.Request;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Class description...
 */
public class ElevatorSubsystem implements Runnable {

    public ElevatorSubsystem(BlockingQueue<ConcurrentMap<Request.Key, Object>> incomingRequests,
                             BlockingQueue<ConcurrentMap<Request.Key, Object>> outgoingRequests) {

    }

    @Override
    public void run() {

    }

}
