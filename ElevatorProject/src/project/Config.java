package project;

/**
 * A utility class for configuration.
 */
public final class Config {

    public static final int NUMBER_OF_ELEVATORS       = 1;
    public static final int NUMBER_OF_FLOORS          = 4;
    public static final int REQUEST_QUEUE_CAPACITY    = 10;
    public static final String REQUEST_BATCH_FILENAME = "input.txt";

    private Config() {
        throw new UnsupportedOperationException(); // prevents instantiation via reflection
    }

}