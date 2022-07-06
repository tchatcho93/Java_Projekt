package eit.linecode.exception;

/**
 * This is a runtime exception which can be thrown when the start-delimiter of a 8b/10b encoded stream is wrong.
 */
public class StartOfPacketException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the type {@code StartOfPacketException}
     * with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public StartOfPacketException(String message) {
        super(message);
    }
}

