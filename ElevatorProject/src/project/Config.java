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
    public static final UDPInfo SCHEDULER_UDP_INFO = new UDPInfo(5000, 5001);
    public static final UDPInfo[] ELEVATORS_UDP_INFO = {new UDPInfo(5002, 5003), new UDPInfo(5004, 5005), new UDPInfo(5006, 5007)};
    public static final UDPInfo[] FLOORS_UDP_INFO = {new UDPInfo(5008, 5009), new UDPInfo(5010, 5011), new UDPInfo(5012, 5013)};

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation from reflection
    }

}