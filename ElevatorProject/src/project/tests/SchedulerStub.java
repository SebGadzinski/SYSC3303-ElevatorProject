package project.tests;

import static project.Config.SCHEDULER_UDP_INFO;

import java.net.InetAddress;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.AbstractSubsystem;
import project.systems.ElevatorSubsystem;
import project.systems.FloorSubsystem;
import project.systems.Scheduler;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;

public class SchedulerStub extends AbstractSubsystem {

	protected SchedulerStub(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
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
