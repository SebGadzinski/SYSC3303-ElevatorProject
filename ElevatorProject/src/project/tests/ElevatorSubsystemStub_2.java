package project.tests;

import java.net.InetAddress;

import project.systems.AbstractSubsystem;
import project.utils.datastructs.Request;

public class ElevatorSubsystemStub_2 extends AbstractSubsystem {

    public ElevatorSubsystemStub_2(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
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