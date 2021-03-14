package project.utils.datastructs;

import java.net.InetAddress;

public class UDPInfo {

    private final InetAddress inetAddress;
    private final int inSocketPort;
    private final int outSocketPort;

    public UDPInfo(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
        this.inetAddress = inetAddress;
        this.inSocketPort = inSocketPort;
        this.outSocketPort = outSocketPort;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public int getInSocketPort() {
        return inSocketPort;
    }

    public int getOutSocketPort() {
        return outSocketPort;
    }

    @Override
    public String toString() {
        return "UDPInfo: \n"
                + "inSocketPort: " + inSocketPort + "\n"
                + "inetAddress: " + inetAddress + "\n"
                + "outSocketPort: " + outSocketPort;
    }

}
