package project;

import project.utils.datastructs.Pair;
import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * A utility class for configuration.
 *
 * @author Paul Roode
 * @author Sebastian Gadzinksi
 * @version Iteration 4
 */
public final class Config {

    public static final int ELEVATOR_DOOR_TIME = 5; // seconds
    public static final int ELEVATOR_PASSENGER_WAIT_TIME = 10; // seconds
    public static final int NUMBER_OF_ELEVATORS = 4;
    public static final int NUMBER_OF_FLOORS = 2;
    public static final int REQUEST_QUEUE_CAPACITY = 10;
    public static final String REQUEST_BATCH_FILENAME = "input.txt";

    // Faults config
    //--------------------------------------------------------------------------------------------
    public static final int FIX_ELEVATOR_TIME = 5000; // milliseconds
    public static final int TIMER_TIMEOUT = 20000; // milliseconds
    public static final boolean FAULT_PRINTING = true;

    //--------------------------------------------------------------------------------------------

    // UDP config
    //--------------------------------------------------------------------------------------------
    private static final ConcurrentSkipListSet<Integer> ports = new ConcurrentSkipListSet<>();
    public static final UDPInfo[] FLOORS_UDP_INFO = new UDPInfo[NUMBER_OF_FLOORS];
    public static final UDPInfo[] ELEVATORS_UDP_INFO = new UDPInfo[NUMBER_OF_ELEVATORS];
    public static InetAddress localhost;

    static {
        for (int port = 5600; port < 5700; ++port) ports.add(port);
        try {
            localhost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(1);
        }
        for (int floorPort = 0; floorPort < NUMBER_OF_FLOORS; floorPort++) FLOORS_UDP_INFO[floorPort] = new UDPInfo(localhost, getPort(), getPort());
        for (int elevatorPort = 0; elevatorPort < NUMBER_OF_ELEVATORS; elevatorPort++) ELEVATORS_UDP_INFO[elevatorPort] = new UDPInfo(localhost, getPort(), getPort());
    }

    public static final UDPInfo SCHEDULER_UDP_INFO = new UDPInfo(localhost, getPort(), getPort());

    //--------------------------------------------------------------------------------------------

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

}
