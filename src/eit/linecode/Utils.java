package eit.linecode;

import java.util.Random;

/**
 * <p>
 * This {@code Utils}class contains significant methods which
 * is needed by both the {@code Server} and {@code Client} classes
 */
public class Utils {

    private static final int EIGHTBITS = 8; // number of bits in a byte
    private static final int ALLBITSON = 0xFF; // turns all bits in a byte on

    /**
     * Generates random bytes and places them into a byte array.
     * <p>
     * The number of random bytes produced is defined by the input argument.
     *
     * @param i The number of bytes to be generated
     * @return the byte array filled with random bytes
     */
    public static byte[] getRandomBytes(int i) {
        byte[] data = new byte[i];
        new Random().nextBytes(data);
        return data;
    }

    /**
     * this function converts an integer into an array of bytes
     *
     * @param value the number of  byte to be generated
     * @return a representation of the {@code value}-integer as an array of bytes
     */
    public static byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >> EIGHTBITS + EIGHTBITS + EIGHTBITS),
                (byte) (value >> EIGHTBITS + EIGHTBITS),
                (byte) (value >> EIGHTBITS),
                (byte) value};
    }

    /**
     * this function convert a byte array to Integer
     *
     * @param bytes the array to be converted
     * @return an integer representation of the {@code bytes} array
     */
    public static int toInteger(byte[] bytes) {

        return ((bytes[0] & ALLBITSON) << EIGHTBITS + EIGHTBITS + EIGHTBITS)
                | ((bytes[1] & ALLBITSON) << EIGHTBITS + EIGHTBITS)
                | ((bytes[2] & ALLBITSON) << EIGHTBITS) | (bytes[2 + 1] & ALLBITSON);
    }


    /**
     * A 32bit representation of flag and reserved bytes needed by the Dataframe
     */
    public enum Flag {
        ACK(0x80000000), // the ackflag
        DEFAULT(0x00000000), // a default flag
        FIN(0x20000000), // the fin flag
        FINACK(0xa0000000), // the finack flag
        SYN(0x40000000), // the syn flag
        SYNACK(0xc0000000); // the synack flag.
        private final int value; //The value of the flag.

        /**
         * Constructs a Flag.
         *
         * @param value the value of the flag.
         */
        Flag(int value) {
            this.value = value;
        }


        /**
         * Returns the value of the Flag enum
         *
         * @return the value of the Flag
         */
        public int getValue() {
            return value;
        }


        /**
         * Returns a Flag if the value param is a valid flag
         *
         * @param value The value of the searched Flag
         * @return Returns a Flag if the value param is a valid flag else null
         */
        public static Flag getValueOf(int value) {
            for (Flag e : values()) {
                if (e.value == value) {
                    return e;
                }
            }
            return null;
        }
    }
}
