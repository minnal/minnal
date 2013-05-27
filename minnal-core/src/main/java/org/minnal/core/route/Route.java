/**
 * 
 */
package org.minnal.core.route;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.config.RouteConfiguration;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author ganeshs
 *
 */
public class Route {
	
	private RoutePattern routePattern;
	
	private HttpMethod method;
	
	private Action action;
	
	@JsonIgnore
	private RouteConfiguration configuration;
	
	private Map<String, String> attributes = new HashMap<String, String>();
	
	private Set<QueryParam> queryParams = new HashSet<QueryParam>();
	
	public Route(RoutePattern routePattern, HttpMethod method, Action action,
			RouteConfiguration configuration, Map<String, String> attributes, Set<QueryParam> queryParams) {
		this.routePattern = routePattern;
		this.method = method;
		this.action = action;
		this.configuration = configuration;
		this.attributes = attributes;
		this.queryParams = queryParams;
	}

	/**
	 * @return the configuration
	 */
	public RouteConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @return the routePattern
	 */
	public RoutePattern getRoutePattern() {
		return routePattern;
	}

	/**
	 * @return the method
	 */
	public HttpMethod getMethod() {
		return method;
	}

	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	/**
	 * @return the queryParams
	 */
	public Set<QueryParam> getQueryParams() {
		return queryParams;
	}
}