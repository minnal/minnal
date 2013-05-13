/**
 * 
 */
package org.minnal.core.server;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;


/**
 * @author ganeshs
 *
 */
public class MessageContext {

	private ServerRequest request;
	
	private ServerResponse response;
	
	private Application<ApplicationConfiguration> application;
	
	private Route route;

	/**
	 * @param request
	 * @param response
	 */
	public MessageContext(ServerRequest request, ServerResponse response) {
		this.request = request;
		this.response = response;
	}
	
	/**
	 * @return the request
	 */
	public ServerRequest getRequest() {
		return request;
	}

	/**
	 * @return the response
	 */
	public ServerResponse getResponse() {
		return response;
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

	/**
	 * @return the route
	 */
	public Route getRoute() {
		return route;
	}

	/**
	 * @param route the route to set
	 */
	public void setRoute(Route route) {
		this.route = route;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MessageContext [request=").append(request)
				.append(", response=").append(response)
				.append(", application=").append(application)
				.append(", route=").append(route).append("]");
		return builder.toString();
	}

}
