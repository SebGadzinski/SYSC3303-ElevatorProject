package project.systems;

import project.utils.datastructs.Request;

import java.io.*;
import java.net.*;

/**
 * Abstracts the UDP mechanism.
 *
 * @author Paul Roode
 * @version Iteration 3
 */
public abstract class AbstractSubsystem {

    private static final int MAX_PACKET_SIZE = 200; // bytes

    private DatagramSocket inSocket, outSocket;

    /**
     * Initializes the inlet and outlet datagram sockets.
     */
    protected AbstractSubsystem() {

        try {

            inSocket = new DatagramSocket();
            outSocket = new DatagramSocket();

            // bind the sockets to arbitrary available socket addresses
            inSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));
            outSocket.bind(new InetSocketAddress(InetAddress.getLocalHost(), 0));

        } catch (SocketException | UnknownHostException e) {

            e.printStackTrace();
            System.exit(1);

        }

    }

    /**
     * Serializes the given Request, then sends it in a DatagramPacket to the given target.
     *
     * @param request The Request to serialize and send in a DatagramPacket.
     * @param target  The target; i.e., receiver of the packet.
     */
    protected void sendRequest(Request request, AbstractSubsystem target) {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream;

        try {

            // serialize the given Request
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(request);
            objectOutputStream.flush();
            byte[] requestInBytes = byteArrayOutputStream.toByteArray();

            // send the serialized Request in a DatagramPacket to the target
            sendPacket(requestInBytes, requestInBytes.length, target.getInSocketAddress(), outSocket);

        } catch (IOException ioe) {

            ioe.printStackTrace();

        } finally {

            try {
                byteArrayOutputStream.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
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

        } catch (IOException | ClassNotFoundException e) {

            e.printStackTrace();

        } finally {

            try {
                if (objectInput != null) {
                    objectInput.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
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

        DatagramPacket packetToSend = getNewPacket(packetData, packetLength, destinationSocketAddress);
        try {
            sendingSocket.send(packetToSend);
        } catch (IOException ioe) {
            ioe.printStackTrace();
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
        DatagramPacket receivedPacket = getNewPacket(packetData, packetData.length);
        try {
            receivingSocket.receive(receivedPacket);
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.exit(1);
        }
        return receivedPacket;
    }

    /**
     * Gets a new DatagramPacket.
     *
     * @param packetData   The data constituting the packet.
     * @param packetLength The length of the packet.
     * @return a new DatagramPacket.
     */
    private DatagramPacket getNewPacket(byte[] packetData, int packetLength) {
        return new DatagramPacket(packetData, packetLength);
    }

    /**
     * Gets a new DatagramPacket.
     *
     * @param packetData               The data constituting the packet.
     * @param packetLength             The length of the packet.
     * @param destinationSocketAddress The packet's destination socket address.
     * @return a new DatagramPacket.
     */
    private DatagramPacket getNewPacket(byte[] packetData, int packetLength, SocketAddress destinationSocketAddress) {
        return new DatagramPacket(packetData, packetLength, destinationSocketAddress);
    }

    /**
     * Returns the address of the inlet DatagramSocket of a concrete specialization of this class.
     *
     * @return the address of the inlet DatagramSocket of a concrete specialization of this class.
     */
    private SocketAddress getInSocketAddress() {
        return inSocket.getLocalSocketAddress();
    }

}
