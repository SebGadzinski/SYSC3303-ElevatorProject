package project.tests;

import static project.Config.SCHEDULER_UDP_INFO;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.AbstractSubsystem;
import project.systems.Scheduler;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;

public class ElevatorSubsystemStub_2 extends AbstractSubsystem {

	public ElevatorSubsystemStub_2(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
		super(inetAddress, inSocketPort, outSocketPort);
	}
	
    public int sendRequestPub(Request request,  InetAddress destinationInetAddress, int destinationSocketPort) {
    	try {
    		this.sendRequest(request, destinationInetAddress, destinationSocketPort);
    	} catch (Exception e) {
    		return 0;
    	}
    	return 1;
    }
}