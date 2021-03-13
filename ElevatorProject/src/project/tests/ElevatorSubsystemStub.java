// package project.tests;

// import java.io.IOException;
// import java.net.DatagramPacket;
// import java.net.DatagramSocket;
// import java.net.InetAddress;
// import java.net.SocketException;
// import java.net.UnknownHostException;

// import project.Config;
// import project.state_machines.ElevatorStateMachine.ElevatorDirection;
// import project.systems.AbstractSubsystem;
// import project.utils.datastructs.FileRequest;
// import project.utils.datastructs.Request;

// public class ElevatorSubsystemStub extends AbstractSubsystem {

// 	DatagramPacket receivePacket;
// 	DatagramPacket sendPacket;
// 	DatagramSocket socket;

// 	public ElevatorSubsystemStub(InetAddress inetAddress, int inSocketPort, int outSocketPort) {
// 		super(inetAddress, inSocketPort, outSocketPort);
// 	}

// 	public static void main(String[] args) {
// 		ElevatorSubsystemStub test = new ElevatorSubsystemStub(Config.FLOORS_UDP_INFO[0].getInetAddress(), 6969, 7070);
// 		FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, Request.Source.FLOOR_SUBSYSTEM);
		
// 		while (true) {
// 			//send file requests to the elevator subsystems
// 			test.sendRequest(fileRequest, Config.ELEVATORS_UDP_INFO[0].getInetAddress(), Config.ELEVATORS_UDP_INFO[0].getInSocketPort());
// 			test.sendRequest(fileRequest, Config.ELEVATORS_UDP_INFO[1].getInetAddress(), Config.ELEVATORS_UDP_INFO[1].getInSocketPort());
// 			test.sendRequest(fileRequest, Config.ELEVATORS_UDP_INFO[2].getInetAddress(), Config.ELEVATORS_UDP_INFO[2].getInSocketPort());
			
// 			System.out.println("Elevator Stub sending a fileRequest");

// 			try {
// 				Thread.sleep(3000);
// 			} catch (InterruptedException e) {
// 				// TODO Auto-generated catch block
// 				e.printStackTrace();
// 			}
			
// 		}
// 	}
// }