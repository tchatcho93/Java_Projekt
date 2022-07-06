package eit.linecode.exception;

/**
 * this is a runtime exception which can be thrown when
 * 1. a stream length without Its delimiters is Invalid
 * 2. a 10b word of a 8b/10b encoded stream is faulty and thus cannot be decoded.
 */
public class DecodeException extends RuntimeException {

    /**
     * Constructs a new runtime exception with the type {@code DecodeException} with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public DecodeException(String message) {
        super(message);
    }
}
