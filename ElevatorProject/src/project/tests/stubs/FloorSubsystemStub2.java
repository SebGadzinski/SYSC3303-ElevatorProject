package project.tests.stubs;

import static project.state_machines.ElevatorStateMachine.ElevatorDirection.UP;

import java.net.InetAddress;

import project.systems.AbstractSubsystem;
import project.systems.FloorSubsystem;
import project.utils.datastructs.ElevatorArrivalRequest;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.ReadRequestResult;
import project.utils.datastructs.Request;
import project.utils.datastructs.UDPInfo;

public class FloorSubsystemStub2 extends FloorSubsystem{

	private int arrived = 0;
	
    public FloorSubsystemStub2(UDPInfo floorUDPInfo, int elevatorNumber, UDPInfo schedulerUDPInfo) {
        super(floorUDPInfo, elevatorNumber, schedulerUDPInfo);
        
        this.arrived = 0;
    }
    
    public int sendRequestPub(Request request, InetAddress destinationInetAddress, int destinationSocketPort) {
        try {
            this.sendRequest(request, destinationInetAddress, destinationSocketPort);
        } catch (Exception e) {
            return 0;
        }
        return 1;
    }
    
    public Request waitForRequestPub() {
    	return this.waitForRequest();
    }
    
    public int handleRequestPub(Request request) {
    	Request returnRequest = this.handleRequest(request);
    	if(returnRequest instanceof ElevatorArrivalRequest) {
    		return 1;
    	}
    	return 0;
    }
    
    public int receivedArrival() {
    	return this.arrived;
    }
    
    @Override
    public void run() {
    	
    	FileRequest fileRequest = new FileRequest("18:17:17.020 ", 0, UP, 3, this.getSource());            
    	if (fileRequest.getOriginFloor() == this.floorNo) {
    		if (fileRequest.getOriginFloor() > fileRequest.getDestinationFloor()) {
    			this.downLamp = true;
    		} else {
    			this.upLamp = true;
    		}
    		this.sendRequestPub(fileRequest, schedulerUDPInfo.getInetAddress(), schedulerUDPInfo.getInSocketPort());

    	}

        while (!(this.waitForRequest() instanceof ElevatorArrivalRequest)) {
            this.arrived = 1;
        }
    }
}
