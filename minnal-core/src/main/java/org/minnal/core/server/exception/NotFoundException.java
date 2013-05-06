/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class NotFoundException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super(HttpResponseStatus.NOT_FOUND);
	}
	
	public NotFoundException(String message) {
		super(HttpResponseStatus.NOT_FOUND, message);
	}
	
	public NotFoundException(String message, Throwable throwable) {
		super(HttpResponseStatus.NOT_FOUND, message, throwable);
	}
	
	public NotFoundException(Throwable throwable) {
		super(HttpResponseStatus.NOT_FOUND, throwable);
	}

}
