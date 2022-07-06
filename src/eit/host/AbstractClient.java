package eit.host;

import eit.host.exception.HostException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Arrays;

/**
 * This class represents an abstract client for sending and receiving datagram packets.
 *
 * <p>A client is the sending point for data packets.
 */
abstract class AbstractClient {

    private DatagramSocket socket;
    private InetAddress address;
    private final static int SERVERPORT = 65000;
    private final static int TIMEOUT = 500;

    /**
     * Constructs a client datagram socket and binds it to any available port
     * on the local host machine.
     * It tries to resolve the local host IP address.
     * This is achieved by retrieving the name of the host from the system.
     * Enable the socket timeout with 500 milliseconds.
     * With this option set, a call to receive() for this DatagramSocket
     * will block for only this amount of time.
     * If the timeout expires, a <strong>java.net.SocketTimeoutException</strong> is raised.
     *
     * <p>If there is a security manager,
     * its {@code checkListen} method is first called
     * with 0 as its argument to ensure the operation is allowed.
     * This could result in a SecurityException.
     *
     * @throws SocketException      if the socket could not be opened,
     *                              or the socket could not bind to the specified local port.
     * @throws UnknownHostException if the local host name could not
     *                              be resolved into an address.
     * @throws SecurityException    if a security manager exists and its
     *                              {@code checkListen} method doesn't allow the operation.
     */
    protected AbstractClient() throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName("127.0.0.1");
        socket.setSoTimeout(TIMEOUT);

    }

    /**
     * Sends and receives a datagram packet from this socket.
     * <p>
     * Sends a datagram packet from this socket. The
     * {@code DatagramPacket} includes information indicating the
     * data to be sent, its length, the IP address of the remote host,
     * and the port number on the remote host.
     * <p>
     * Receives a datagram packet from this socket. It blocks
     * until a datagram is received.
     *
     * @param buffer the buffer as <code>byte[]</code> to be sent.
     * @return received data as <code>byte[]</code>.
     * @throws SocketTimeoutException if timeout of 500 milliseconds has expired.
     * @throws IOException            if an I/O error occurs.
     */
    protected byte[] sendRecv(byte[] buffer) throws SocketTimeoutException, IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVERPORT);
        socket.send(packet);
        packet = new DatagramPacket(buffer, buffer.length);
        socket.receive(packet);
        byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());
        return receivedData;
    }

    /**
     * Sends a datagram packet from this socket. The
     * {@code DatagramPacket} includes information indicating the
     * data to be sent, its length, the IP address of the remote host,
     * and the port number on the remote host.
     *
     * @param buffer the buffer as <code>byte[]</code> to be sent.
     * @throws IOException if an I/O error occurs.
     */
    protected void send(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, SERVERPORT);
        socket.send(packet);
    }

    /**
     * Closes this client datagram socket and releases
     * any system resources associated with it.
     * <p>
     * If the socket is already closed then invoking this
     * method has no effect.
     */
    public void close() {
        socket.close();
    }

    /**
     * This method establishes a connection between the client and the server.
     * It sends a {@code SYN} flag to the server and awaits a {@code SYNACK} flag from the server.
     * The connection is successful if the {@code SYNACK} flag is received from the server.
     *
     * @return True if the request to establish a connection with the server was successful
     * @throws SocketTimeoutException if the specified socket timeout duration expires.
     * @throws IOException            if an I/O error occurs.
     */
    public abstract boolean connect() throws SocketTimeoutException, IOException;

    /**
     * This method makes a disconnect request to the server.
     * It creates a dataframe with the {@code FIN} flag and sends it to the server.
     * If the server accepts the request, the connection between the server and the client will be terminated.
     *
     * @return true if the disconnect request is accepted by the server.
     * @throws SocketTimeoutException if the specified socket timeout duration expires.
     * @throws IOException            if an I/O error occurs.
     */
    public abstract boolean disconnect() throws SocketTimeoutException, IOException;

    /**
     * This method is used to send data to the server.
     * It builds a dataframe with the appropriate
     * sequence- and acknowledgement number and sends it to the server.
     *
     * @param data the data as {@code byte[]} to be sent
     * @throws HostException when the max number of trails ist exceeded without a response from the server.
     */
    public abstract void sendData(byte[] data);


}
