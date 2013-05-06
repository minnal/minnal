/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author ganeshs
 *
 */
public class ForbiddenException extends ApplicationException {

	private static final long serialVersionUID = 1L;

	public ForbiddenException() {
		super(HttpResponseStatus.FORBIDDEN);
	}
	
	public ForbiddenException(String message) {
		super(HttpResponseStatus.FORBIDDEN, message);
	}
	
	public ForbiddenException(String message, Throwable throwable) {
		super(HttpResponseStatus.FORBIDDEN, message, throwable);
	}
	
	public ForbiddenException(Throwable throwable) {
		super(HttpResponseStatus.FORBIDDEN, throwable);
	}

}
