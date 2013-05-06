/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class UnauthorizedException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public UnauthorizedException() {
		super(HttpResponseStatus.UNAUTHORIZED);
	}
	
	public UnauthorizedException(String message) {
		super(HttpResponseStatus.UNAUTHORIZED, message);
	}
	
	public UnauthorizedException(String message, Throwable throwable) {
		super(HttpResponseStatus.UNAUTHORIZED, message, throwable);
	}
	
	public UnauthorizedException(Throwable throwable) {
		super(HttpResponseStatus.UNAUTHORIZED, throwable);
	}

}
