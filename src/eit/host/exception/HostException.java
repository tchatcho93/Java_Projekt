package eit.host.exception;

/**
 * It is thrown when the max number of trials to connect to the server leads to no avail.
 */
public class HostException extends RuntimeException {
    /**
     * Constructs a new runtime exception with the type {@code HostException} with the specified detail message.
     *
     * @param msg the detail message. The detail message is saved for
     *            later retrieval by the {@link #getMessage()} method.
     */
    public HostException(String msg) {
        super(msg);
    }
}
