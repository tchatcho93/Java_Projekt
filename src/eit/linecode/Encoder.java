package eit.linecode;

/**
 * The Encoder Class implements the encoder functionality of the 8b/10B encoder.
 *<p>
 * this Method converts 8 bit( Bytes) code group into 10 bit( for example +-+++--+-+) code. the byte is split up into
 * the 3 most significant bits and the 5 least significant bits.
 * the 3 bit Block is encoded in 4 bits and the 5 bit block is encoded in 6 bit.
 * and the combined to 10 bit encoded value.
 */
public class Encoder {
    // the code table object
    private CodeTable codeTable;
    // the value for neutral disparity
    private static final int NEUTRAL = 0;
    // toggles between rdPlus and rdMinus
    private boolean isRdPlus;

    /**
     * <p>
     * It initialises the encoder object. It requires a CodeTable object containing
     * the tables and helper Methods needed by the 8B/10B encoder
     *
     * @param codeTable the Code-table needed for the 8B/10B encoding process.
     */
    public Encoder(CodeTable codeTable) {
        this.codeTable = codeTable;
        isRdPlus = false;
    }

    /**
     * <p>
     * With the help of the code-table object, the rules and principles of the 8B/10B code
     * each 8-bit code in the byte array is converted into a 10-bit code
     * Each byte in the array is mapped to an 8-bit Code which is later encoded to a 10-bit code.
     * Every encoded package is wrapped with a start and an end package consisting of 10-bit code each.
     *
     * @param data An array containing the bytes(information) to be encoded.
     * @return     encoded 10-bit code with end and start delimiters.
     */
    public String encode(byte[] data) {
        // contains the encoded result
        StringBuilder result = new StringBuilder();
        // the current disparity of 10 bit word.
        int currentDisparity = NEUTRAL;
        result.append(codeTable.getStartDelimiter(this.isRdPlus));

        for (byte idx : data) {
            if (currentDisparity != NEUTRAL) {
                isRdPlus = !isRdPlus;
            }
            if (isRdPlus) {
                result.append(codeTable.getRdPlus(idx));
                currentDisparity = codeTable.getDisparityPlus(idx);
            } else {
                result.append(codeTable.getRdMinus(idx));
                currentDisparity = codeTable.getDisparityMinus(idx);
            }
        }
        if (currentDisparity != NEUTRAL) {
            isRdPlus = !isRdPlus;
        }
        result.append(codeTable.getEndDelimiter(isRdPlus));
        return result.toString();
    }
}
