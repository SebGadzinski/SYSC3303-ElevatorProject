package project.tests.stubs;

import project.systems.Scheduler;
import project.utils.datastructs.Request;
import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;

public class SchedulerStub extends Scheduler {

    public SchedulerStub(UDPInfo schedulerUDPInfo, UDPInfo[] elevatorsUDPInfo, UDPInfo[] floorsUDPInfo) {
        super(schedulerUDPInfo, elevatorsUDPInfo, floorsUDPInfo);
    }

    public int sendRequestPub(Request request, InetAddress destinationInetAddress, int destinationSocketPort) {
        try {
            this.sendRequest(request, destinationInetAddress, destinationSocketPort);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }

}