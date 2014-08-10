/**
 * 
 */
package org.minnal.core.server;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;


/**
 * @author ganeshs
 *
 */
public class MessageContext {
	
	private URI baseUri;

	private FullHttpRequest request;
	
	private FullHttpResponse response;
	
	private String matchedRoute;
	
	private Application<ApplicationConfiguration> application;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	/**
	 * @param request
	 * @param response
	 */
	public MessageContext(FullHttpRequest request, URI baseUri) {
		this.request = request;
		this.baseUri = baseUri;
	}
	
	/**
	 * @return the baseUri
	 */
	public URI getBaseUri() {
		return baseUri;
	}

	/**
	 * @return the request
	 */
	public FullHttpRequest getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public FullHttpResponse getResponse() {
		return response;
	}

	/**
	 * @param response the response to set
	 */
	public void setResponse(FullHttpResponse response) {
		this.response = response;
	}

	/**
	 * @return the application
	 */
	public Application<ApplicationConfiguration> getApplication() {
		return application;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication(Application<ApplicationConfiguration> application) {
		this.application = application;
	}
	
	public void addAttribute(String name, Object value) {
		this.attributes.put(name, value);
	}
	
	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}
	
	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	/**
	 * @return the matchedRoute
	 */
	public String getMatchedRoute() {
		return matchedRoute;
	}

	/**
	 * @param matchedRoute the matchedRoute to set
	 */
	public void setMatchedRoute(String matchedRoute) {
		this.matchedRoute = matchedRoute;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageContext [request=").append(request)
				.append(", response=").append(response)
				.append(", application=").append(application);
		return builder.toString();
	}

}
