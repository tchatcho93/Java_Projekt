package eit.host;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

import edu.fra.uas.oop.Terminal;

/**
 * This class represents an abstract server for receiving and answering datagram packets.
 * <p>
 * A server is the receiving point for data packets.
 * This class provides a skeletal implementation of such a server.
 * To implement a server with it specific behavior, the programmer
 * needs only to extend this class and implement the methods <code>accept</code>
 * and <code>isDisconnected</code>.
 * <p>
 * This class is a subclass of <code>Thread</code>. This
 * subclass overrides the <code>run</code> method of class
 * <code>Thread</code>. An instance of the subclass can then be
 * allocated and started.
 * <p>
 * For example, a server receive datagrams and echo them until
 * a message "end" is received could be written as follows:
 * <hr><pre>
 * class Server extends AbstractServer {
 * &commat;Override
 * protected void accept(String received) throws IOException {
 * sendPacket(received);
 * }
 *
 * &commat;Override
 * protected boolean isDisconnect(String received) {
 * if (received.equals("end")) {
 * return true;
 * } else {
 * return false;
 * }
 * }
 * }
 * </pre><hr>
 * <p>
 * The following code would then create a server thread and start it running:
 * <blockquote><pre>
 * Server echoServer = new Server();
 * echoServer.start();
 * </pre></blockquote>
 */

abstract class AbstractServer extends Thread {
    private DatagramSocket socket;
    private boolean active = true;
    private InetAddress address;
    private int port;
    private byte[] buf = new byte[1500];
    private final static int SERVERPORT = 65000;

    /**
     * Constructs a server datagram socket and binds it to port 65000
     * on the local host machine.
     *
     * @throws IOException if an I/O error occurs.
     */
    protected AbstractServer() throws IOException {
        socket = new DatagramSocket(SERVERPORT);
    }

    /**
     * This method is required to be public, but should never be
     * called explicitly. Also, the method should not be modified.
     * <p>
     * It performs the main run loop to execute the servers behavior.
     * <p>
     * For adapting the behavior the methods <code>accept</code>
     * and <code>isDisconnected</code> should be used in the inherited class.
     */
    public void run() {
        active = true;
        while (active) {
            try {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                address = packet.getAddress();
                port = packet.getPort();
                byte[] receivedData = Arrays.copyOf(packet.getData(), packet.getLength());
                accept(receivedData);
                if (isDisconnect(receivedData)) {
                    active = false;
                    continue;
                }
            } catch (SocketException e) {
                active = false;
            } catch (IOException e) {
                Terminal.printError(e.toString());
                active = false;
            }
        }
        socket.close();
    }

    /**
     * Sends a datagram packet from this server socket.
     *
     * @param buffer the data as <code>byte[]</code> to be sent.
     * @throws IOException if an I/O error occurs.
     */
    protected void send(byte[] buffer) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        socket.send(packet);
    }

    /**
     * Answers a <code>boolean</code> indicating whether the server
     * is active (<code>true</code>) or not (<code>false</code>)
     * after has been started.
     * <p>
     * If the server is not active anymore it will not receive any
     * further datagram packets and a new server thread must created
     * and started:
     * <pre>
     * Server echoServer = new Server();
     * echoServer.start();
     * </pre>
     *
     * @return a <code>boolean</code> true if this server is active; false otherwise.
     */
    public boolean isActive() {
        return isAlive();
    }

    /**
     * Closes this server datagram socket and releases
     * any system resources associated with it.
     * <p>
     * If the socket is already closed then invoking this
     * method has no effect.
     */
    public void close() {
        socket.close();
    }

    /**
     * This method analyses the data received from the client.
     * It checks the received data and reacts accordingly
     *
     * @param receivedData the data as {@code byte[]} received from the client.
     * @throws IOException if an I/O error occurs.
     */
    protected abstract void accept(byte[] receivedData) throws IOException;

    /**
     * Checks if the clients message is to want to disconnect.
     *
     * @param receivedData contains the data as {@code byte[]} received from the client.
     * @return true if the client sent a FIN flag.
     * @throws IOException if an I/O error occurs.
     */
    protected abstract boolean isDisconnect(byte[] receivedData) throws IOException;

    /**
     * Reads the bytes received from the client.
     * The buffer is emptied after this method is called.
     *
     * @return the data as {@code byte[]} received from the client.
     */
    public abstract byte[] read();

}
