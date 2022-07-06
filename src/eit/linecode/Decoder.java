package eit.linecode;

import eit.linecode.exception.DecodeException;
import eit.linecode.exception.EndOfPacketException;
import eit.linecode.exception.StartOfPacketException;

/**
 * Decoder Class this Class implements the decode functionality  of the 8B/10B decoder.
 * <p>
 * 1.check if our start Delimiter and end delimiter corresponds the values of our Control Charaters Kx.y.
 * If it doesn't match ,it returns an exception .
 * 2.check if the remaining stream without our start delimiter and end delimiter can be decoded,
 * if not, it returns an exception.
 * 3. if our stream without the start delimiter and end delimiter can be decoding,
 * we start the decoding Process.
 */
public class Decoder {
    private CodeTable codeTable;
    private static final int WORDLENGTH = 10;
    private static final int NEUTRAL = 0;

    /**
     * <p>
     * It initialises the decoder object. It requires a CodeTable object containing
     * the tables and helper Methods needed by the 8B/10B decoder
     *
     * @param codeTable the Code-table needed for the 8B/10B decoding process
     */
    public Decoder(CodeTable codeTable) {
        this.codeTable = codeTable;
    }

    /**
     * <p>
     * This function  analysis a word (preferably 10bit) and checks if it is a 10bit RdPlus start delimiter
     * in the kx.y table.
     *
     * @param word is a 10bit code
     * @return true if the word is an rdPlus start
     */
    private boolean isStartRDPlus(String word) {
        return word.equals(codeTable.getStartDelimiter(true));
    }

    /**
     * <p>
     * This function  analysis a word (preferably 10bit) and checks if it is a 10bit RdMinus start delimiter
     * in the kx.y table.
     *
     * @param word word is a 10bit code
     * @return false if the word is an RdMinus start
     */
    private boolean isStartRdMinus(String word) {
        return word.equals(codeTable.getStartDelimiter(false));
    }

    /**
     * This function  analysis a word (preferably 10bit) and checks if it is a 10bit RdPlus end delimiter
     * in the kx.y table.
     *
     * @param word word is a 10bit code
     * @return true if the word is an RdPlus end
     */
    private boolean isEndRDPlus(String word) {
        return word.equals(codeTable.getEndDelimiter(true));
    }

    /**
     * <p>
     * This function  analysis a word (preferably 10bit) and checks if it is a 10bit RdMinus end delimiter
     * in the kx.y table
     *
     * @param word word is a 10bit code
     * @return false if the word is an RdMinus end
     */
    private boolean isEndRdMinus(String word) {
        return word.equals(codeTable.getEndDelimiter(false));
    }

    /**
     * Returns a substring of the original String. The substring begins at the 10th index;
     *
     * @param word The original string.
     * @return A string containing a substring of the original string beginning  at the 10th  index.
     */
    private String removeTen(String word) {
        return word.substring(WORDLENGTH);
    }

    /**
     * Returns the first ten characters of the original string.
     *
     * @param word The original string.
     * @return A string containing the first ten characters of the original string.
     */
    private String getTen(String word) {
        return word.substring(NEUTRAL, WORDLENGTH);
    }

    /**
     * <p>
     * With the help of the code-table object a string containing data encoded, this method
     * decrypts a 8b/10b encoded data back to it original byte data stream.
     *
     * @param data a string containing a stream of 8b/10b encoded words with its end and start delimiters
     * @return a byte array containing the decrypted word using 8b/10b decoding rules
     * @throws StartOfPacketException when the start-delimiter of the encoded word is faulty
     * @throws EndOfPacketException   when the end-delimiter the encoded word is faulty
     * @throws DecodeException        when the word to be decoded is faulty.
     */
    public byte[] decode(String data) {

        String start_delimiter = data.substring(NEUTRAL, WORDLENGTH);
        String remainingData = removeTen(data);
        boolean isRdPlus;
        if (isStartRdMinus(start_delimiter)) {
            isRdPlus = false;
        } else if (isStartRDPlus(start_delimiter)) {
            isRdPlus = true;
        } else {
            throw new StartOfPacketException("Start of Packet not detected!!!");
        }
        if (data.length() % WORDLENGTH != NEUTRAL) throw new DecodeException("Invalid data detected!!!");
        byte[] result = new byte[(data.length() - (WORDLENGTH + WORDLENGTH)) / WORDLENGTH];
        int counter = 0;
        while (remainingData.length() > WORDLENGTH) {
            String next = getTen(remainingData);
            if (isRdPlus) {
                result[counter] = codeTable.getRdPlusPosition(next);
            } else {
                result[counter] = codeTable.getRdMinusPosition(next);
            }
            if (codeTable.calculateDisparity(next) != NEUTRAL) {
                isRdPlus = !isRdPlus;
            }
            remainingData = removeTen(remainingData);
            counter++;
        }
        String endDelimiter = getTen(remainingData);
        if (isRdPlus) {
            if (!isEndRDPlus(endDelimiter)) {
                throw new EndOfPacketException("End of Packet not detected!!!");
            }
        } else {
            if (!isEndRdMinus(endDelimiter)) {
                throw new EndOfPacketException("End of Packet not detected!!!");
            }
        }
        return result;
    }
}

