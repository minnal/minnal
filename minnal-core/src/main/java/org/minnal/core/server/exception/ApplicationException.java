/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.MinnalException;

/**
 * @author ganeshs
 *
 */
public class ApplicationException extends MinnalException {
	
	private HttpResponseStatus status;
	
	private static final long serialVersionUID = 1L;

	public ApplicationException(HttpResponseStatus status) {
		this(status, status.getReasonPhrase(), null);
	}

	public ApplicationException(HttpResponseStatus status, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
	}

	public ApplicationException(HttpResponseStatus status, String message) {
		this(status, message, null);
	}

	public ApplicationException(HttpResponseStatus status, Throwable cause) {
		this(status, status.getReasonPhrase(), cause);
	}

	/**
	 * @return the status
	 */
	public HttpResponseStatus getStatus() {
		return status;
	}

}
