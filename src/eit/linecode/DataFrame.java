package eit.linecode;

import java.util.Arrays;


import eit.linecode.Utils.Flag;

/**
 * <p>
 * This class is used to construct a DataFrame which is needed for the communication between the client and the server.
 * It has helpful constructors to create a dataframe object.
 * It also has a method {@code getDataFrame} which restructures the {@code DataFrame}
 * object into a dataframe byte array.
 */
public class DataFrame {
    private static final int BYTES = 4;
    private static final int FRAMELENGHT = 12; // length of frame without the payload

    private final int seqNumber; // the sequence number
    private final int ackNumber; // the acknowledgement number
    private final Flag flagAndRes; // the flag and reserved bytes of the dataframe
    private final byte[] payload; // the payload of the dataframe

    /**
     * Constructs a {@code DataFrame} using the provided parameters.
     *
     * @param seqNumber the sequence number of the {@code DataFrame} object.
     * @param ackNumber the acknowledgment number of the {@code DataFrame} object.
     * @param flag      the flag and reserved bytes of the {@code DataFrame} object.
     * @param payload   the payload of the {@code DataFrame} object.
     */
    public DataFrame(int seqNumber, int ackNumber, Flag flag, byte[] payload) {
        this.seqNumber = seqNumber;
        this.ackNumber = ackNumber;
        this.flagAndRes = flag;
        this.payload = payload;
    }

    /**
     * Primarily constructs a {@code DataFrame} Object for needed establishing and clearing a connection to the server.
     *
     * @param flag The flag and reserved bytes of the dataframe to be constructed
     */
    public DataFrame(Flag flag) {
        this(0, 0, flag, new byte[0]);
    }

    /**
     * Constructs a {@code DataFrame} Object from the {@code DataFrame} array.
     *
     * @param dataFrame consists of the dataframe as {@code byte[]}.
     */
    public DataFrame(byte[] dataFrame) {
        byte[] seq = new byte[BYTES];
        byte[] ack = new byte[BYTES];
        byte[] flagbytes = new byte[BYTES];
        byte[] data = new byte[dataFrame.length - FRAMELENGHT];
        System.arraycopy(dataFrame, 0, seq, 0, seq.length);
        System.arraycopy(dataFrame, BYTES, ack, 0, ack.length);
        System.arraycopy(dataFrame, BYTES + BYTES, flagbytes, 0, flagbytes.length);
        if (data.length > 0) {
            System.arraycopy(dataFrame, FRAMELENGHT, data, 0, data.length);
        }
        this.seqNumber = Utils.toInteger(seq);
        this.ackNumber = Utils.toInteger(ack);
        this.flagAndRes = Flag.getValueOf(Utils.toInteger(flagbytes));
        this.payload = data;
    }

    /**
     * Reconstructs and returns the {@code DataFrame} object as an array of bytes.
     *
     * @return The byte array consisting of the dataframe object.
     */
    public byte[] getDataFrame() {
        byte[] seq = Utils.intToByteArray(this.seqNumber);
        byte[] ack = Utils.intToByteArray(this.ackNumber);
        byte[] flag = Utils.intToByteArray(this.flagAndRes.getValue());

        byte[] dataframe = new byte[seq.length + ack.length + flag.length + payload.length];
        System.arraycopy(seq, 0, dataframe, 0, seq.length);
        System.arraycopy(ack, 0, dataframe, seq.length, ack.length);
        System.arraycopy(flag, 0, dataframe, seq.length + ack.length, flag.length);
        System.arraycopy(payload, 0, dataframe, seq.length + ack.length + flag.length, payload.length);
        return dataframe;
    }

    /**
     * Returns the sequence number of the dataframe.
     *
     * @return the sequence number of the dataframe.
     */
    public int getSeqNumber() {
        return seqNumber;
    }

    /**
     * Returns the acknowledgment number of the dataframe.
     *
     * @return the acknowledgment number of the dataframe.
     */
    public int getAckNumber() {
        return ackNumber;
    }

    /**
     * Returns the flag and reserved flags of the dataframe.
     *
     * @return the flag and reserved flags of the dataframe.
     */
    public Flag getFlagAndRes() {
        return flagAndRes;
    }

    /**
     * Returns the payload of the dataframe.
     *
     * @return the payload as {@code byte[]} of the dataframe.
     */
    public byte[] getPayload() {
        return payload;
    }

    /**
     * Returns a string representation of the {@code DataFrame} Object, displaying the relevant information.
     *
     * @return a string representation of the Dataframe.
     */
    @Override
    public String toString() {
        return "".concat("seq_number -> " + this.seqNumber + "\n").
                concat("ackNumber -> " + this.ackNumber + "\n").
                concat("flagAndRes -> " + this.flagAndRes.name() + "\n").
                concat("payload -> " + Arrays.toString(this.payload) + "\n");
    }

}
