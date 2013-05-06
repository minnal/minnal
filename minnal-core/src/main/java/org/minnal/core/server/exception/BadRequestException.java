/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class BadRequestException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super(HttpResponseStatus.BAD_REQUEST);
	}
	
	public BadRequestException(String message) {
		super(HttpResponseStatus.BAD_REQUEST, message);
	}
	
	public BadRequestException(String message, Throwable throwable) {
		super(HttpResponseStatus.BAD_REQUEST, message, throwable);
	}
	
	public BadRequestException(Throwable throwable) {
		super(HttpResponseStatus.BAD_REQUEST, throwable);
	}

}
