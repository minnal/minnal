/**
 * 
 */
package org.minnal.core.server.exception;

import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;

import com.google.common.base.Joiner;
import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class NotAcceptableException extends ApplicationException {
	
	private Set<MediaType> expectedTypes;

	private static final long serialVersionUID = 1L;
	
	public NotAcceptableException(Set<MediaType> expectedTypes) {
		super(HttpResponseStatus.NOT_ACCEPTABLE);
		this.expectedTypes = expectedTypes;
	}

	@Override
	public void handle(Response response) {
		super.handle(response);
		response.addHeader(HttpHeaders.Names.ACCEPT, Joiner.on(", ").join(expectedTypes));
	}

}
