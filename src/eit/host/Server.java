package eit.host;

import java.io.IOException;
import eit.linecode.DataFrame;
import eit.linecode.Decoder;
import eit.linecode.Encoder;
import eit.linecode.Utils.Flag;

/**
 * <p>
 * This class represents a server for receiving and answering datagram packets.
 * This class is a subclass of {@code AbstractServer}. This
 * subclass overrides the {@code accept}, {@code isDisconnect}, and {@code reacts} methods
 * of the AbstractClass {@code AbstractServer}. The AbstractClass {@code AbstractServer}
 * is also a subclass of {@code Thread} which means an instance of this class can then be
 * allocated and started using the inherited method {@code start}.
 * <p>
 * A server is the receiving point for data packets.
 */
public class Server extends AbstractServer {

    abc abc
    private final Decoder decoder; // The decoder is needed for decrypting the dataframes received from client.
    private byte[] buffer; // contains the bytes read from the client.
    private final Encoder encoder; //The encoder is needed for encrypting the dataframes before sending them the client

    /**
     * Initialises the Server Object.
     * <p> Requires an {@code Encoder} and {@code Decoder} object to initialise successfully.
     *
     * @param encoder An encoder needed for encrypting dataframes before being sent to the client.
     * @param decoder A decoder needed for decrypting dataframes received from the client.
     * @throws IOException if an I/O error occurs.
     */
    public Server(Encoder encoder, Decoder decoder) throws IOException {
        super();
        buffer = new byte[0];
        this.decoder = decoder;
        this.encoder = encoder;
    }

    /**
     * <p>
     * This method analyses the data received from the client.
     * It checks the received data analyses the {@code Flag} bytes and reacts accordingly
     *
     * @param receivedData the data as {@code byte[]} received from the client.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected void accept(byte[] receivedData) throws IOException {
        byte[] res = decoder.decode(new String(receivedData));
        DataFrame dataFrame = new DataFrame(res);
        DataFrame response;
        switch (dataFrame.getFlagAndRes()) {
            case DEFAULT:
                response = new DataFrame(dataFrame.getSeqNumber(), dataFrame.getAckNumber() + 1, Flag.DEFAULT,
                        new byte[0]);
                if (dataFrame.getPayload().length > 0) {
                    byte[] tmp = new byte[buffer.length + dataFrame.getPayload().length];
                    System.arraycopy(buffer, 0, tmp, 0, buffer.length);
                    System.arraycopy(dataFrame.getPayload(), 0, tmp, buffer.length,
                            dataFrame.getPayload().length);
                    this.buffer = tmp;
                }
                send(encoder.encode(response.getDataFrame()).getBytes());
                break;
            case FIN:
                response = new DataFrame(Flag.FINACK);
                send(encoder.encode(response.getDataFrame()).getBytes());
                break;
            case SYN:
                response = new DataFrame(Flag.SYNACK);
                send(encoder.encode(response.getDataFrame()).getBytes());
                break;
            case ACK:
            case SYNACK:
            case FINACK:
            default:
                break;

        }
    }

    /**
     * Analyses the content of the {@code receivedData} and checks if the client want to disconnect.
     *
     * @param receivedData contains the data as {@code byte[]} received from the client.
     * @return true if the client sent a FIN flag.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    protected boolean isDisconnect(byte[] receivedData) throws IOException {
        DataFrame dataFrame = new DataFrame(decoder.decode(new String(receivedData)));
        return dataFrame.getFlagAndRes().equals(Flag.FIN);
    }

    /**
     * <p>
     * Reads the bytes received from the client.
     * The buffer is emptied after this method is called.
     *
     * @return the data as {@code byte[]} received from the client.
     */
    @Override
    public byte[] read() {
        byte[] tmp = buffer;
        buffer = new byte[0];
        return tmp;
    }
}
