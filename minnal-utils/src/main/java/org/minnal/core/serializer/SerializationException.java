/**
 * 
 */
package org.minnal.core.serializer;

/**
 * @author ganeshs
 *
 */
public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor
	 */
	public SerializationException() {
	}

	/**
	 * @param message
	 */
	public SerializationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public SerializationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public SerializationException(String message, Throwable cause) {
		super(message, cause);
	}
}
