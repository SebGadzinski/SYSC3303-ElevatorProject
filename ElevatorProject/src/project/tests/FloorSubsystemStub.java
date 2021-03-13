package project.tests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import project.Config;
import project.state_machines.ElevatorStateMachine.ElevatorDirection;
import project.systems.FloorSubsystem;
import project.utils.datastructs.FileRequest;
import project.utils.datastructs.Request;
import project.utils.datastructs.SubsystemSource;
import project.utils.datastructs.SubsystemSource.Subsystem;

public class FloorSubsystemStub {

	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	DatagramSocket socket;

	public FloorSubsystemStub() {
		try {
			socket = new DatagramSocket(Config.SCHEDULER_UDP_INFO.getInSocketPort());
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void receiveAndAcknowledge(byte[] dataToSend) {

		byte data[] = new byte[1000];
		receivePacket = new DatagramPacket(data, data.length);

		// Receiveing a packet from floorSubSytem
		try {
			socket.receive(receivePacket);
			System.out.println("Packet received from the FLOOR SUBSYSTEM");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("packet achnowledgement was not received by host");
			System.exit(1);
		}


		sendPacket = new DatagramPacket(dataToSend, dataToSend.length, Config.FLOORS_UDP_INFO[0].getInetAddress(), Config.FLOORS_UDP_INFO[0].getInSocketPort());

		// sending the Datagram packet
		try {
			socket.send(sendPacket);
			System.out.println("SENDING ACK BACK");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error in client asking for host data");
			System.exit(1);
		}

	}

	public byte[] sendRequest(Request request) {

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream;
		byte[] requestInBytes = null;
		
		try {

			// serialize the given Request
			objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
			objectOutputStream.writeObject(request);
			objectOutputStream.flush();
			requestInBytes = byteArrayOutputStream.toByteArray();
			

		} catch (IOException ioe) {

			ioe.printStackTrace();

		} finally {
			
			try {
				byteArrayOutputStream.close();
				return requestInBytes;
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}

		}
		return null;
	}

	public static void main(String[] args) {
		System.out.println("Waiting for packet from a floor subsystem");
		
		FloorSubsystem floor = new FloorSubsystem(Config.FLOORS_UDP_INFO[0].getInetAddress(), 100, 101, 0);
 		FileRequest fileRequest = new FileRequest("18:17:17.020", 0, ElevatorDirection.UP, 3, floor.getSource());
		FloorSubsystemStub test = new FloorSubsystemStub();

		while (true) {
			byte[] tmp = test.sendRequest(fileRequest);
			test.receiveAndAcknowledge(tmp);
		}
	}

}
