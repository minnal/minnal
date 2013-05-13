/**
 * 
 */
package org.minnal.core.server.exception;

import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.Response;

import com.google.common.base.Joiner;

/**
 * @author ganeshs
 *
 */
public class MethodNotAllowedException extends ApplicationException {
	
	private Set<HttpMethod> allowedMethods;

	private static final long serialVersionUID = 1L;

	public MethodNotAllowedException(Set<HttpMethod> allowedMethods) {
		super(HttpResponseStatus.METHOD_NOT_ALLOWED);
		this.allowedMethods = allowedMethods;
	}
	
	@Override
	public void handle(Response response) {
		super.handle(response);
		response.addHeader(HttpHeaders.Names.ALLOW, Joiner.on(", ").join(allowedMethods));
	}

	/**
	 * @return the allowedMethods
	 */
	public Set<HttpMethod> getAllowedMethods() {
		return allowedMethods;
	}
	
}
