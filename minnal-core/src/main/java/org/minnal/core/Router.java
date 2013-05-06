/**
 * 
 */
package org.minnal.core;

import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.exception.NotFoundException;


/**
 * @author ganeshs
 *
 */
public class Router {
	
	private ApplicationMapping applicationMapping;
	
	public Router(ApplicationMapping applicationMapping) {
		this.applicationMapping = applicationMapping;
	}
	
	public void route(MessageContext context) {
		try {
			Route route = resolve(context);
			route.getAction().invoke(context.getRequest(), context.getResponse());
		} catch (Exception e) {
			context.getApplication().getExceptionHandler().handle(context.getRequest(), context.getResponse(), e);
		} finally {
			if (context.getResponse().getStatus() == HttpResponseStatus.PROCESSING) {
				context.getResponse().setStatus(HttpResponseStatus.OK);
			}
		}
	}

	/**
	 * @return the applicationMapping
	 */
	public ApplicationMapping getApplicationMapping() {
		return applicationMapping;
	}
	
	protected Route resolve(MessageContext context) {
		Application<ApplicationConfiguration> application = applicationMapping.resolve(context.getRequest());
		if (application == null) {
			throw new NotFoundException("Request path not found");
		}
		context.setApplication(application);
		context.getRequest().setApplicationPath(application.getPath()); // NOTE: This should be done before resolving the action
		
		Route route = application.getRoutes().resolve(context.getRequest());
		if (route == null) {
			Set<HttpMethod> allowedMethods = application.getRoutes().getAllowedMethods(context.getRequest());
			if (! allowedMethods.isEmpty()) {
				throw new NotFoundException("Unsupported http method. Expected - " + allowedMethods);
			}
			throw new NotFoundException("Request path not found");
		}
		Map<String, String> parameters = route.getRoutePattern().match(context.getRequest().getRelativePath());
		if (parameters == null) {
			// TODO shouldn't get here as we have already resolved the route. throw a not found exception ??? 
		}
		context.getRequest().addHeaders(parameters);
		context.setRoute(route);
		return route;
	}
	
}
