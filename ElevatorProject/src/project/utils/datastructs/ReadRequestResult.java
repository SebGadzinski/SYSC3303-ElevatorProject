package project.utils.datastructs;

import java.util.concurrent.ConcurrentMap;

/**
 * Multi-return auxiliary to FloorSubsystem::readRequest().
 *
 * @author Paul Roode
 */
public final class ReadRequestResult {

    private final ConcurrentMap<Request.Key, Object> request;
    private final boolean isThereAnotherRequest;

    public ReadRequestResult(ConcurrentMap<Request.Key, Object> request, boolean isThereAnotherRequest) {
        this.request = request;
        this.isThereAnotherRequest = isThereAnotherRequest;
    }

    /**
     * Gets the read-in request.
     *
     * @return The read-in request.
     */
    public ConcurrentMap<Request.Key, Object> getRequest() {
        return request;
    }

    /**
     * Gets whether there is another request.
     *
     * @return true if there is another request, false otherwise.
     */
    public boolean isThereAnotherRequest() {
        return isThereAnotherRequest;
    }

}
