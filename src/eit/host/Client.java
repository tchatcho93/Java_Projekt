package eit.host;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import eit.host.exception.HostException;
import eit.linecode.DataFrame;
import eit.linecode.Decoder;
import eit.linecode.Encoder;
import eit.linecode.Utils.Flag;

/**
 * This class represents a client for sending and receiving datagram packets.
 * It contains helpful methods like {@code connect} and {@code disconnect} to
 * successfully establish or terminate a connection to the server respectively.
 * <p>
 * A client is the sending point for data packets.
 */
public class Client extends AbstractClient {
    private int seq = 1; // sequence number of the dataframe to be sent.
    private int ack = 0; // acknowledgement number of the dataframe.
    private final static int MAXTRIALS = 4; // maximum number of times to try to send a package.
    private final Encoder encoder; // needed to encrypt the dataframes from server.
    private final Decoder decoder; // needed to decrypt the dataframes before sending them to the server.

    /**
     * Initialises the client Object.
     * <p> Requires an {@code Encoder} and {@code Decoder} object to initialise successfully.
     *
     * @param encoder An encoder needed for encrypting dataframes before being sent to the server.
     * @param decoder A decoder needed for decrypting dataframes received from the server.
     * @throws SocketException      if the socket could not be opened,
     *                              or the socket could not bind to the specified local port.
     * @throws UnknownHostException if the local host name could not
     *                              be resolved into an address.
     */
    public Client(Encoder encoder, Decoder decoder) throws SocketException, UnknownHostException {
        super();
        this.encoder = encoder;
        this.decoder = decoder;
    }

    /**
     * This method establishes a connection between the client and the server.
     * It sends a {@code SYN} flag to the server and awaits a {@code SYNACK} flag from the server.
     * <p>
     * The connection is successful if the {@code SYNACK} flag is received from the server.
     *
     * @return True if the request to establish a connection with the server was successful
     * @throws SocketTimeoutException if the specified socket timeout duration expires.
     * @throws IOException            if an I/O error occurs.
     */
    @Override
    public boolean connect() throws SocketTimeoutException, IOException {
        DataFrame data = new DataFrame(Flag.SYN);
        byte[] encodedData = encoder.encode(data.getDataFrame()).getBytes();
        DataFrame response = new DataFrame(decoder.decode(new String(sendRecv(encodedData))));
        if (response.getFlagAndRes().equals(Flag.SYNACK)) {
            data = new DataFrame(Flag.ACK);
            send(encoder.encode(data.getDataFrame()).getBytes());
            return true;
        }
        return false;
    }

    /**
     * This method makes a disconnect request to the server.
     * It creates a dataframe with the {@code FIN} flag and sends it to the server.
     * <p>
     * If the server accepts the request, the connection between the server and the client will be terminated.
     *
     * @return true if the disconnect request is accepted by the server.
     * @throws SocketTimeoutException if the specified socket timeout duration expires.
     * @throws IOException            if an I/O error occurs.
     */
    @Override
    public boolean disconnect() throws SocketTimeoutException, IOException {
        DataFrame data = new DataFrame(Flag.FIN);
        byte[] encodedData = encoder.encode(data.getDataFrame()).getBytes();
        DataFrame response = new DataFrame(decoder.decode(new String(sendRecv(encodedData))));
        return response.getFlagAndRes().equals(Flag.FINACK);
    }

    /**
     *<p>
     *  This method is used to send data to the server.
     * It builds a dataframe with the appropriate
     * sequence- and acknowledgement number and sends it to the server.
     *
     * @param data the data as {@code byte[]} to be sent
     * @throws HostException when the max number of trails ist exceeded without a response from the server.
     */
    @Override
    public void sendData(byte[] data) {
        DataFrame dataFrame = new DataFrame(seq, ack, Flag.DEFAULT, data);
        byte[] encodedData = encoder.encode(dataFrame.getDataFrame()).getBytes();
        int timeout = 0;
        while (true) {
            try {
                DataFrame response = new DataFrame(decoder.decode(new String(sendRecv(encodedData))));
                if (isValid(response)) {
                    this.seq++;
                    this.ack++;
                    break;
                }
                if (timeout++ == MAXTRIALS) {
                    throw new HostException("Max entries exceeded");
                }
            } catch (IOException e) {
                if (timeout++ == MAXTRIALS) {
                    throw new HostException("Max retries exceeded");
                }
            }
        }
    }

    /**
     *<p>
     *  This method is primarily used to validate the dataframe received from the server.
     * It compares its acknowledgement and sequence number and returns its validity.
     *
     * @param dataFrame the {@code DataFrame} object to be validated.
     * @return true if the dataframe is valid else false.
     */
    private boolean isValid(DataFrame dataFrame) {
        return dataFrame.getAckNumber() == this.ack + 1 && dataFrame.getSeqNumber() == this.seq;
    }
}
