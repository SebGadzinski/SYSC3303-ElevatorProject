package project.utils.datastructs;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides a thread-safe representation of a request.
 *
 * @author Paul Roode
 */
public class Request {

    /**
     * Keys to access the data constituting a request.
     */
    public enum Key {
        TIME, ORIGIN_FLOOR, DIRECTION, DESTINATION_FLOOR
    }

    /**
     * Returns a thread-safe hashmap representation of a request.
     *
     * @param <K> The key.
     * @param <V> The value.
     * @return A thread-safe hashmap representation of a request.
     */
    public static <K, V> ConcurrentHashMap<K, V> newInstance() {
        return new ConcurrentHashMap<>();
    }

}
