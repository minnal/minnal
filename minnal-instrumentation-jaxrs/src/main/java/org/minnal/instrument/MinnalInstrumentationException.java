/**
 * 
 */
package org.minnal.instrument;

/**
 * @author ganeshs
 *
 */
public class MinnalInstrumentationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public MinnalInstrumentationException() {
	}

	/**
	 * @param message
	 */
	public MinnalInstrumentationException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public MinnalInstrumentationException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public MinnalInstrumentationException(String message, Throwable cause) {
		super(message, cause);
	}
}
