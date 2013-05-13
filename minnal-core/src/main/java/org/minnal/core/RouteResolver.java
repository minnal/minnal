/**
 * 
 */
package org.minnal.core;

import java.util.Map;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.exception.NotFoundException;

/**
 * @author ganeshs
 *
 */
public class RouteResolver {
	
	private ApplicationMapping applicationMapping;
	
	public RouteResolver(ApplicationMapping applicationMapping) {
		this.applicationMapping = applicationMapping;
	}

	public Route resolve(MessageContext context) {
		Application<ApplicationConfiguration> application = applicationMapping.resolve(context.getRequest());
		if (application == null) {
			throw new NotFoundException("Request path not found");
		}
		context.setApplication(application);
		context.getRequest().setApplicationPath(application.getPath()); // NOTE: This should be done before resolving the action
		
		Route route = application.getRoutes().resolve(context.getRequest());
		context.getRequest().setResolvedRoute(route);
		context.getResponse().setResolvedRoute(route);
		
		Map<String, String> parameters = route.getRoutePattern().match(context.getRequest().getRelativePath());
		if (parameters == null) {
			// TODO shouldn't get here as we have already resolved the route. throw a not found exception ??? 
		}
		
		context.getRequest().addHeaders(parameters);
		context.setRoute(route);
		return route;
	}
}
