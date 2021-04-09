package project.tests.stubs;

import project.systems.ElevatorSubsystem;
import project.utils.datastructs.Request;
import project.utils.datastructs.UDPInfo;

import java.net.InetAddress;

public class ElevatorSubsystemStub extends ElevatorSubsystem {

    public ElevatorSubsystemStub(UDPInfo elevatorUDPInfo, int elevatorNumber, UDPInfo schedulerUDPInfo) {
        super(elevatorUDPInfo, elevatorNumber, schedulerUDPInfo);
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