package project.tests.stubs;

import java.net.InetAddress;

import project.systems.AbstractSubsystem;

import project.utils.datastructs.Request;

public class SchedulerStub extends AbstractSubsystem {

    public SchedulerStub(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
        super(inetAddress, inSocketPort, outSocketPort);
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
