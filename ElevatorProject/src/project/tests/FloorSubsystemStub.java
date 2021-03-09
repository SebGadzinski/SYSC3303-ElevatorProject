package project.tests;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class FloorSubsystemStub {

	DatagramPacket receivePacket;
	DatagramPacket sendPacket;
	DatagramSocket socket;

	public FloorSubsystemStub() {
		try {
			socket = new DatagramSocket(69);
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public void receiveAndAcknowledge() {

		byte data[] = new byte[1000];
		receivePacket = new DatagramPacket(data, data.length);

		// Receiveing a packet from floorSubSytem
		try {
			socket.receive(receivePacket);
			System.out.println("packet received");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("packet achnowledgement was not received by host");
			System.exit(1);
		}

		// creating acknowledgment to be sent to the host
		byte[] request = new byte[] { 1 }; // creating request message

		sendPacket = new DatagramPacket(request, request.length, receivePacket.getAddress(), receivePacket.getPort());

		// sending the Datagram packet
		try {
			socket.send(sendPacket);
			System.out.println("Acknowledgment sent");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("error in client asking for host data");
			System.exit(1);
		}

	}

	public static void main(String[] args) {
		FloorSubsystemStub test = new FloorSubsystemStub();

		while (true) {
			test.receiveAndAcknowledge();
		}
	}

}
