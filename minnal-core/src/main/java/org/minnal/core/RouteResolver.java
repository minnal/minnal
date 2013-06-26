/**
 * 
 */
package org.minnal.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.Route;
import org.minnal.core.server.MessageContext;
import org.minnal.core.server.exception.NotFoundException;
import org.minnal.core.util.Comparators;
import org.minnal.core.util.HttpUtil;

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
		
		ResourceClass resourceClass = resolveResource(application, context.getRequest());
		context.setResourceClass(resourceClass);
		Route route = application.getRoutes(resourceClass).resolve(context.getRequest());
		
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
	
	protected ResourceClass resolveResource(Application<ApplicationConfiguration> application, Request request) {
		Map<String, ResourceClass> resources = getResources(application);
		String path = request.getUri().getPath();
		for (Entry<String, ResourceClass> entry : resources.entrySet()) {
			if (path.startsWith(HttpUtil.structureUrl(application.getConfiguration().getBasePath()) + entry.getKey())) {
				return entry.getValue();
			}
		}
		throw new NotFoundException();
	}
	
	private Map<String, ResourceClass> getResources(Application<ApplicationConfiguration> application) {
		Map<String, ResourceClass> resources = new TreeMap<String, ResourceClass>(Comparators.LENGTH_COMPARATOR);
		for (ResourceClass resource : application.getResources()) {
			resources.put(resource.getBasePath(), resource);
		}
		return resources;
	}
}
