/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class InternalServerErrorException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public InternalServerErrorException() {
		super(HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	public InternalServerErrorException(String message) {
		super(HttpResponseStatus.INTERNAL_SERVER_ERROR, message);
	}
	
	public InternalServerErrorException(String message, Throwable throwable) {
		super(HttpResponseStatus.INTERNAL_SERVER_ERROR, message, throwable);
	}
	
	public InternalServerErrorException(Throwable throwable) {
		super(HttpResponseStatus.INTERNAL_SERVER_ERROR, throwable);
	}

}
