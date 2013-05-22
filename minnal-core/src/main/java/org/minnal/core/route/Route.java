/**
 * 
 */
package org.minnal.core.route;

import java.util.HashMap;
import java.util.Map;

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
	
	public Route(RoutePattern routePattern, HttpMethod method, Action action,
			RouteConfiguration configuration, Map<String, String> attributes) {
		this.routePattern = routePattern;
		this.method = method;
		this.action = action;
		this.configuration = configuration;
		this.attributes = attributes;
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
}