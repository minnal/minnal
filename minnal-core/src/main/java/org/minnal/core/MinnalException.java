/**
 * 
 */
package org.minnal.core;

/**
 * @author ganeshs
 *
 */
public class MinnalException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MinnalException() {
	}

	/**
	 * @param message
	 */
	public MinnalException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MinnalException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MinnalException(String message, Throwable cause) {
		super(message, cause);
	}
}
