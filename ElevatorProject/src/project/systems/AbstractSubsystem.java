package project.systems;

import project.utils.datastructs.Request;

import java.io.*;
import java.net.*;

/**
 * Abstracts the UDP mechanism and request serialization.
 *
 * @author Paul Roode
 * @version Iteration 3
 */
public abstract class AbstractSubsystem {

    protected static final int MAX_PACKET_SIZE = 200; // bytes

    protected DatagramSocket inSocket, outSocket;

    /**
     * Initializes the inlet and outlet datagram sockets.
     *
     * @param inetAddress   The IP address of the concrete subsystem.
     * @param inSocketPort  The inlet socket port number.
     * @param outSocketPort The outlet socket port number.
     */
    protected AbstractSubsystem(InetAddress inetAddress, int inSocketPort, int outSocketPort) {

        try {

            inSocket = new DatagramSocket(null);
            outSocket = new DatagramSocket(null);

            // bind the sockets to the given socket addresses
            inSocket.bind(new InetSocketAddress(inetAddress, inSocketPort));
            outSocket.bind(new InetSocketAddress(inetAddress, outSocketPort));

        } catch (SocketException se) {

            se.printStackTrace();
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
    protected void sendRequest(Request request,
                               InetAddress destinationInetAddress,
                               int destinationSocketPort) {

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
