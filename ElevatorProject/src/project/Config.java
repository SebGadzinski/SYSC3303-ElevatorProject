package project;

import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A utility class for configuration.
 *
 * @author Paul Roode
 * @author Sebastian Gadzinksi
 * @version Iteration 5
 */
public final class Config {

    public static final int ELEVATOR_DOOR_TIME = 3; // seconds
    public static final int ELEVATOR_PASSENGER_WAIT_TIME = 10; // seconds
    public static final int MIN_ELEVATOR_FLOOR_TO_FLOOR_TRAVEL_TIME = 400; // milliseconds
    public static final int MAX_ELEVATOR_FLOOR_TO_FLOOR_TRAVEL_TIME = 700; // milliseconds
    public static final int NUMBER_OF_ELEVATORS = 4;
    public static final int NUMBER_OF_FLOORS = 22;
    public static final int NUMBER_OF_REQUESTS = 8;
    public static final int REQUEST_QUEUE_CAPACITY = 10;
    public static final String REQUEST_BATCH_FILENAME = "input.txt";

    // Test config
    //----------------------------------------------------------------------------------------
    public static final int TEST_NUMBER_OF_ELEVATORS = 4;
    public static final int TEST_NUMBER_OF_FLOORS = 4;
    public static final int TEST_NUMBER_OF_REQUESTS = 3;
    public static final String TEST_REQUEST_BATCH_FILENAME = "testInput.txt";
    //----------------------------------------------------------------------------------------

    // Faults config
    //----------------------------------------------------------------------------------------
    public static final int FIX_ELEVATOR_TIME = 5000; // milliseconds
    public static final int TIMER_TIMEOUT = 20000; // milliseconds
    public static final boolean FAULT_PRINTING = true;
    //----------------------------------------------------------------------------------------

    // UDP config
    //----------------------------------------------------------------------------------------
    private static final ConcurrentSkipListSet<Integer> ports = new ConcurrentSkipListSet<>();
    public static InetAddress localhost;
    public static final UDPInfo[] ELEVATORS_UDP_INFO = new UDPInfo[NUMBER_OF_ELEVATORS];
    public static final UDPInfo[] FLOORS_UDP_INFO = new UDPInfo[NUMBER_OF_FLOORS];
    public static UDPInfo SCHEDULER_UDP_INFO;

    static {
        for (int port = 5600; port < 5800; ++port) {
            ports.add(port);
        }
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (int elevatorNum = 0; elevatorNum < NUMBER_OF_ELEVATORS; ++elevatorNum) {
            ELEVATORS_UDP_INFO[elevatorNum] = new UDPInfo(localhost, getPort(), getPort());
        }
        for (int floorNum = 0; floorNum < NUMBER_OF_FLOORS; ++floorNum) {
            FLOORS_UDP_INFO[floorNum] = new UDPInfo(localhost, getPort(), getPort());
        }
        SCHEDULER_UDP_INFO = new UDPInfo(localhost, getPort(), getPort());
    }
    //----------------------------------------------------------------------------------------

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation from reflection
    }

    /**
     * Gets a port if one is available.
     *
     * @return a port if one is available.
     * @throws NullPointerException if there are no available ports.
     */
    public static Integer getPort() throws NullPointerException {
        if (ports.isEmpty()) {
            throw new NullPointerException("No available ports");
        }
        return ports.pollFirst();
    }

    /**
     * Gets a random floor-to-floor travel time for an elevator, in milliseconds.
     *
     * @return a random floor-to-floor travel time for an elevator, in milliseconds.
     */
    public static int getRandomElevatorFloorToFloorTravelTime() {
        return new Random().nextInt(MAX_ELEVATOR_FLOOR_TO_FLOOR_TRAVEL_TIME - MIN_ELEVATOR_FLOOR_TO_FLOOR_TRAVEL_TIME)
                + MIN_ELEVATOR_FLOOR_TO_FLOOR_TRAVEL_TIME;
    }

}