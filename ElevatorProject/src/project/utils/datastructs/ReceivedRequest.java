package project.utils.datastructs;

public class ReceivedRequest {

    public Request requestReceived;

    public ReceivedRequest(Request requestReceived) {
        this.requestReceived = requestReceived;
    }

    public Request getRequestReceived() {
        return requestReceived;
    }

    public void setRequestReceived(Request requestReceived) {
        this.requestReceived = requestReceived;
    }

    @Override
    public String toString() {
        return "ReceivedRequest:\n" + requestReceived;
    }

}
