/**
 * 
 */
package org.minnal.core.route;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Utility class to build a route for a resource class
 * 
 * @author ganeshs
 *
 */
public class RouteBuilder {
	
	private ResourceClass resourceClass;
	
	private Map<String, String> attributes = new HashMap<String, String>();
	
	private RoutePattern pattern;
	
	private RouteConfiguration configuration;
	
	private List<RouteAction> actions = new LinkedList<RouteAction>();
	
	private static Logger logger = LoggerFactory.getLogger(RouteBuilder.class);
	
	public RouteBuilder(ResourceClass resource, String path) {
		this.resourceClass = resource;
		this.pattern = new RoutePattern(path);
		this.configuration = new RouteConfiguration(path, resource.getConfiguration());
	}
	
	public RouteBuilder attributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
		return this;
	}
	
	public RouteBuilder attribute(String key, String value) {
		attributes.put(key, value);
		return this;
	}
	
	public RouteBuilder using(RouteConfiguration configuration) {
		configuration.setParent(resourceClass.getConfiguration());
		this.configuration = configuration;
		return this;
	}
	
	public RouteAction action(HttpMethod httpMethod, String methodName) {
		return action(httpMethod, methodName, Object.class, Object.class);
	}
	
	public RouteAction action(HttpMethod httpMethod, String methodName, Type requestType, Type responseType) {
		return action(httpMethod, methodName, requestType, responseType, Request.class, Response.class);
	}
	
	protected RouteAction action(HttpMethod httpMethod, String methodName, Type requestType, Type responseType, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = resourceClass.getResourceClass().getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			logger.error("Error while getting the method " + methodName + " for the class " + resourceClass.getResourceClass(), e);
			throw new MinnalException(e);
		}
		return action(httpMethod, method, requestType, responseType);
	}
	
	public RouteAction action(HttpMethod httpMethod, Method method) {
		return action(httpMethod, method, Object.class, Object.class);
	}
	
	public RouteAction action(HttpMethod httpMethod, Method method, Type requestType, Type responseType) {
		Action action = null;
		try {
			action = new Action(resourceClass.getResourceClass().newInstance(), method);
		} catch (Exception e) {
			throw new MinnalException("Failed while creating a new isntance of resource class", e);
		}
		RouteAction routeAction = new RouteAction(new Route(pattern, httpMethod, action, requestType, responseType));
		actions.add(routeAction);
		return routeAction;
	}
	
	public List<Route> build() {
		if (actions.isEmpty()) {
			throw new IllegalStateException("Can't build a route without an action. Make sure you have called routeBuilder.action() before invoking build()");
		}
		// Add the options route by default to allow clients to check the available options for this path
		this.actions.add(new RouteAction(new Route(pattern, HttpMethod.OPTIONS, null, Object.class, Object.class)));
		List<Route> routes = new ArrayList<Route>();
		for (RouteAction routeAction : actions) {
			routeAction.getRoute().setConfiguration(configuration);
			routeAction.addAttributes(attributes, false);
			routes.add(routeAction.getRoute());
		}
		return routes;
	}
	
	public Set<HttpMethod> supportedMethods() {
		return Sets.newHashSet(Lists.transform(actions, new Function<RouteAction, HttpMethod>() {
			@Override
			public HttpMethod apply(RouteAction input) {
				return input.getRoute().getMethod();
			}
		}));
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
		result = prime * result
				+ ((resourceClass == null) ? 0 : resourceClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteBuilder other = (RouteBuilder) obj;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		if (resourceClass == null) {
			if (other.resourceClass != null)
				return false;
		} else if (!resourceClass.equals(other.resourceClass))
			return false;
		return true;
	}
}
