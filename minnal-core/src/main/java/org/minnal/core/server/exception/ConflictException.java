/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class ConflictException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public ConflictException() {
		super(HttpResponseStatus.CONFLICT);
	}
	
	public ConflictException(String message) {
		super(HttpResponseStatus.CONFLICT, message);
	}
	
	public ConflictException(String message, Throwable throwable) {
		super(HttpResponseStatus.CONFLICT, message, throwable);
	}
	
	public ConflictException(Throwable throwable) {
		super(HttpResponseStatus.CONFLICT, throwable);
	}

}
