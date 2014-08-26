package org.minnal.generator.exception;

public class MinnalGeneratorException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public MinnalGeneratorException() {
    }

    /**
     * @param message
     */
    public MinnalGeneratorException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public MinnalGeneratorException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public MinnalGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
