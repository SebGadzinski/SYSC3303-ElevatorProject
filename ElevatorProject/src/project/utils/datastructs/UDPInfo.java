package project.utils.datastructs;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class UDPInfo {
    private InetAddress inetAddress;
    private int inSocketPort;
    private int outSocketPort;
    public UDPInfo(int inSocketPort, int outSocketPort) {
        this.inSocketPort = inSocketPort;
        this.outSocketPort = outSocketPort;
        setUpInetAddress();
    }
    
    private void setUpInetAddress() {
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public InetAddress getInetAddress() {
        return inetAddress;
    }
    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = inetAddress;
    }
    public int getInSocketPort() {
        return inSocketPort;
    }
    public void setInSocketPort(int inSocketPort) {
        this.inSocketPort = inSocketPort;
    }
    public int getOutSocketPort() {
        return outSocketPort;
    }
    public void setOutSocketPort(int outSocketPort) {
        this.outSocketPort = outSocketPort;
    }

}
