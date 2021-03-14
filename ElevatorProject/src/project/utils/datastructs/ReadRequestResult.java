package project.utils.datastructs;

/**
 * Multi-return auxiliary to FloorSubsystem::readRequest().
 *
 * @author Paul Roode
 * @version Iteration 1
 */
public final class ReadRequestResult {

    private final FileRequest request;
    private final boolean isThereAnotherRequest;

    public ReadRequestResult(FileRequest request, boolean isThereAnotherRequest) {
        this.request = request;
        this.isThereAnotherRequest = isThereAnotherRequest;
    }

    /**
     * Gets the read-in request.
     *
     * @return The read-in request.
     */
    public FileRequest getRequest() {
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
