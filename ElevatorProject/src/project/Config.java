package project;

import project.utils.datastructs.Pair;
import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A utility class for configuration.
 *
 * @author Paul Roode, Sebastian Gadzinksi
 * @version Iteration 4
 */
public final class Config {

    public static final int ELEVATOR_DOOR_TIME = 5; // seconds
    public static final int ELEVATOR_PASSENGER_WAIT_TIME = 10; // seconds
    public static final int MAX_PASSENGERS_IN_ELEVATOR = 10;
    public static final int NUMBER_OF_ELEVATORS = 3;
    public static final int NUMBER_OF_FLOORS = 4;
    public static final int REQUEST_QUEUE_CAPACITY = 10;
    public static final String REQUEST_BATCH_FILENAME = "input.txt";

    // Faults config
    //--------------------------------------------------------------------------------------------
    public static final int FIX_ELEVATOR_TIME = 5000; // milliseconds
    public static final int TIMER_TIMEOUT = 20000; // milliseconds
    public static final Pair[] faults = {
            new Pair(15, 1),
            new Pair(30, 2),
            new Pair(45, 1)
    };
    //--------------------------------------------------------------------------------------------

    // UDP config
    //--------------------------------------------------------------------------------------------
    private static final ConcurrentSkipListSet<Integer> ports;
    public static InetAddress localhost;

    static {
        ports = new ConcurrentSkipListSet<>();
        for (int port = 5000; port < 5100; ++port) ports.add(port);
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static final UDPInfo SCHEDULER_UDP_INFO = new UDPInfo(localhost, getPort(), getPort());
    public static final UDPInfo[] ELEVATORS_UDP_INFO = {
            new UDPInfo(localhost, getPort(), getPort()),
            new UDPInfo(localhost, getPort(), getPort()),
            new UDPInfo(localhost, getPort(), getPort())
    };
    public static final UDPInfo[] FLOORS_UDP_INFO = {
            new UDPInfo(localhost, getPort(), getPort()),
            new UDPInfo(localhost, getPort(), getPort()),
            new UDPInfo(localhost, getPort(), getPort()),
            new UDPInfo(localhost, getPort(), getPort())
    };
    //--------------------------------------------------------------------------------------------

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation from reflection
    }

    /**
     * Returns a port if one is available.
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

}