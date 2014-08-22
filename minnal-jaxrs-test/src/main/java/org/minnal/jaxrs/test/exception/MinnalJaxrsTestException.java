package org.minnal.jaxrs.test.exception;

public class MinnalJaxrsTestException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MinnalJaxrsTestException() {
    }

    /**
     * @param message
     */
    public MinnalJaxrsTestException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MinnalJaxrsTestException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MinnalJaxrsTestException(String message, Throwable cause) {
        super(message, cause);
    }
}
