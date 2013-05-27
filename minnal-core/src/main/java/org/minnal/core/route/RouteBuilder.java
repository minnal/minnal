/**
 * 
 */
package org.minnal.core.route;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.RouteConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.QueryParam.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	private Map<HttpMethod, Action> actions = new HashMap<HttpMethod, Action>();
	
	private Set<QueryParam> queryParams = new HashSet<QueryParam>();
	
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
	
	public RouteBuilder action(HttpMethod httpMethod, String methodName) {
		return action(httpMethod, methodName, Request.class, Response.class);
	}
	
	protected RouteBuilder action(HttpMethod httpMethod, String methodName, Class<?>... parameterTypes) {
		Method method = null;
		try {
			method = resourceClass.getResourceClass().getMethod(methodName, parameterTypes);
		} catch (Exception e) {
			logger.error("Error while getting the method " + methodName + " for the class " + resourceClass.getResourceClass(), e);
			throw new MinnalException(e);
		}
		return action(httpMethod, method);
	}
	
	public RouteBuilder action(HttpMethod httpMethod, Method method) {
		Action action = null;
		try {
			action = new Action(resourceClass.getResourceClass().newInstance(), method);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		actions.put(httpMethod, action);
		return this;
	}
	
	public List<Route> build() {
		if (actions.isEmpty()) {
			throw new IllegalStateException("Can't build a route without an action. Make sure you have called routeBuilder.action() before invoking build()");
		}
		List<Route> routes = new ArrayList<Route>();
		for (Entry<HttpMethod, Action> entry : actions.entrySet()) {
			routes.add(new Route(pattern, entry.getKey(), entry.getValue(), configuration, attributes, queryParams));
		}
		return routes;
	}
	
	public Set<HttpMethod> supportedMethods() {
		return actions.keySet();
	}
	
	public RouteBuilder queryParam(QueryParam param) {
		queryParams.add(param);
		return this;
	}
	
	public RouteBuilder queryParam(String name) {
		return queryParam(name, "");
	}
	
	public RouteBuilder queryParam(String name, String description) {
		return queryParam(name, Type.string, description);
	}
	
	public RouteBuilder queryParam(String name, QueryParam.Type type, String description) {
		return queryParam(new QueryParam(name, type, description));
	}

	/**
	 * @return the queryParams
	 */
	public Set<QueryParam> getQueryParams() {
		return queryParams;
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
