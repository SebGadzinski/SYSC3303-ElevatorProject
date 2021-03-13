package project.utils.datastructs;

public class AbstractSchedulerSubSystemInfo {
    private String id;
    private UDPInfo udpInfo;

    public AbstractSchedulerSubSystemInfo(String id, UDPInfo udpInfo) {
        this.id = id;
        this.udpInfo = udpInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UDPInfo getUdpInfo() {
        return udpInfo;
    }

    public void setUdpInfo(UDPInfo udpInfo) {
        this.udpInfo = udpInfo;
    }

}
