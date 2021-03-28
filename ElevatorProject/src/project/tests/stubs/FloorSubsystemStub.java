package project.tests.stubs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import project.utils.datastructs.Request;

import static project.Config.*;

public class FloorSubsystemStub {

    DatagramPacket receivePacket;
    DatagramPacket sendPacket;
    DatagramSocket socket;

    public FloorSubsystemStub() {
        try {
            socket = new DatagramSocket(getPort());
            socket.setSoTimeout(1000);
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public boolean receiveAndAcknowledge(byte[] dataToSend) {

        byte[] data = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);

        // receiving a packet from FloorSubsystem
        try {
            socket.receive(receivePacket);
        } catch (IOException e) {
        	return true;
        }
        

        sendPacket = new DatagramPacket(dataToSend, dataToSend.length, localhost, getPort());

        // sending the Datagram packet
        try {
            socket.send(sendPacket);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return false;

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

}
