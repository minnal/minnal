/**
 * 
 */
package org.minnal.core.server.exception;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;

/**
 * @author ganeshs
 *
 */
public class SeeOtherException extends ApplicationException {
	
	private String location;

	public SeeOtherException(String location) {
		super(HttpResponseStatus.SEE_OTHER);
		this.location = location;
	}
	
	@Override
	public void handle(Response response) {
		super.handle(response);
		response.addHeader(HttpHeaders.Names.LOCATION, location);
	}
}
