package project.utils.datastructs;

public abstract class SchedulerSubsystemInfo {

    private String id;
    private UDPInfo udpInfo;

    public SchedulerSubsystemInfo(String id, UDPInfo udpInfo) {
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
