package project;

import java.net.InetAddress;

import project.utils.datastructs.UDPInfo;

/**
 * A utility class for configuration.
 */
public final class Config {

    public static final int NUMBER_OF_ELEVATORS = 1;
    public static final int NUMBER_OF_FLOORS = 4;
    public static final int REQUEST_QUEUE_CAPACITY = 10;
    public static final int ELEVATOR_DOOR_TIME = 5;
    public static final int ELEVATOR_PASSENGER_WAIT_TIME = 10;
    public static final String REQUEST_BATCH_FILENAME = "input.txt";
    public static final UDPInfo SCHEDULER_UDP_INFO = new UDPInfo(500, 600);
    public static final UDPInfo[] ELEVATOR_UDP_INFO = {new UDPInfo(700, 800), new UDPInfo(900, 1000), new UDPInfo(1100, 1200)};
    public static final UDPInfo[] FLOOR_UDP_INFO = {new UDPInfo(1300, 1400), new UDPInfo(1500, 1600), new UDPInfo(1700, 1800)};

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation from reflection
    }

}