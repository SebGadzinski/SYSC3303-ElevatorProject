package project;

import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * A utility class for configuration.
 */
public final class Config {

    public static final int    ELEVATOR_DOOR_TIME           = 5;  // seconds
    public static final int    ELEVATOR_PASSENGER_WAIT_TIME = 10; // seconds
    public static final int    NUMBER_OF_ELEVATORS          = 3;
    public static final int    NUMBER_OF_FLOORS             = 4;
    public static final int    REQUEST_QUEUE_CAPACITY       = 10;
    public static final String REQUEST_BATCH_FILENAME       = "input.txt";

    // UDP config
    //---------------------------------------------------------------------------------------------------------------
    private static InetAddress host1InetAddress;

    static {
        try {
            host1InetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException uhe) {
            uhe.printStackTrace();
            System.exit(1);
        }
    }

    public static final UDPInfo SCHEDULER_UDP_INFO = new UDPInfo(host1InetAddress, 5000, 5001);

    public static final UDPInfo[] ELEVATORS_UDP_INFO = {
            new UDPInfo(host1InetAddress, 5002, 5003),
            new UDPInfo(host1InetAddress, 5004, 5005),
            new UDPInfo(host1InetAddress, 5006, 5007),
            new UDPInfo(host1InetAddress, 5008, 5009)
    };

    public static final UDPInfo[] FLOORS_UDP_INFO = {
            new UDPInfo(host1InetAddress, 5008, 5009),
            new UDPInfo(host1InetAddress, 5010, 5011),
            new UDPInfo(host1InetAddress, 5012, 5013),
            new UDPInfo(host1InetAddress, 5014, 5015)
    };
    //---------------------------------------------------------------------------------------------------------------

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation from reflection
    }

}