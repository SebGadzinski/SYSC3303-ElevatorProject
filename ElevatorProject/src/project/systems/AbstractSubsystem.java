package project.systems;

import project.utils.datastructs.Request;
import project.utils.datastructs.UDPInfo;

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Abstracts the UDP mechanism and request serialization.
 *
 * @author Paul Roode
 * @version Iteration 5
 */
public abstract class AbstractSubsystem implements Runnable {

    protected static final int MAX_PACKET_SIZE = 10000; // bytes

    protected DatagramSocket inSocket, outSocket;

    /**
     * Initializes the inlet and outlet Datagram sockets, binding them to the given socket addresses.
     *
     * @param udpInfo Comprises the socket IP address, and inlet and outlet socket port numbers.
     */
    protected AbstractSubsystem(UDPInfo udpInfo) {
        try {
            inSocket = new DatagramSocket(new InetSocketAddress(udpInfo.getInetAddress(), udpInfo.getInSocketPort()));
            outSocket = new DatagramSocket(new InetSocketAddress(udpInfo.getInetAddress(), udpInfo.getOutSocketPort()));
        } catch (SocketException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Serializes the given Request, then sends it in a DatagramPacket to the given destination.
     *
     * @param request                The Request to serialize and send in a DatagramPacket.
     * @param destinationInetAddress The Request's destination IP address.
     * @param destinationSocketPort  The Request's destination socket port number.
     */
    protected void sendRequest(Request request, InetAddress destinationInetAddress, int destinationSocketPort) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;

        try {

            // serialize the given Request
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            byte[] requestInBytes = byteArrayOutputStream.toByteArray();

            // send the serialized Request in a DatagramPacket to the target
            sendPacket(
                    requestInBytes,
                    requestInBytes.length,
                    new InetSocketAddress(destinationInetAddress, destinationSocketPort),
                    outSocket
            );

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {
                byteArrayOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * Receives, deserializes, and returns a Request, waiting if necessary until one becomes available.
     *
     * @return the Request that was received and deserialized.
     */
    protected Request waitForRequest() {

        // wait for a DatagramPacket comprising a serialized Request
        DatagramPacket receivedPacket = waitForPacket(inSocket);

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(receivedPacket.getData());
        ObjectInput objectInput = null;
        Request receivedRequest = null;

        try {

            // deserialize the received Request
            objectInput = new ObjectInputStream(byteArrayInputStream);
            receivedRequest = (Request) objectInput.readObject();

        } catch (ClassNotFoundException | IOException e) {

            e.printStackTrace();

        } finally {

            try {
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return receivedRequest;

    }

    /**
     * Builds a new DatagramPacket and sends it.
     *
     * @param packetData               The data constituting the packet.
     * @param packetLength             The length of the packet.
     * @param destinationSocketAddress The packet's destination socket address.
     * @param sendingSocket            The socket from which the packet will be sent.
     */
    private void sendPacket(byte[] packetData,
                            int packetLength,
                            SocketAddress destinationSocketAddress,
                            DatagramSocket sendingSocket) {

        DatagramPacket packetToSend = new DatagramPacket(packetData, packetLength, destinationSocketAddress);

        try {
            sendingSocket.send(packetToSend);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }

    /**
     * Receives and returns a DatagramPacket, waiting if necessary until one becomes available.
     *
     * @param receivingSocket The socket that will receive the packet.
     * @return the received packet.
     */
    private DatagramPacket waitForPacket(DatagramSocket receivingSocket) {
        byte[] packetData = new byte[MAX_PACKET_SIZE];
        DatagramPacket receivedPacket = new DatagramPacket(packetData, packetData.length);
        try {
            receivingSocket.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return receivedPacket;
    }

    /**
     * Gets the current timestamp.
     *
     * @return the current timestamp as a String.
     */
    protected String getTimestamp() {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss");
        return timeFormatter.format(new Date());
    }

    @Override
    public abstract void run();

}