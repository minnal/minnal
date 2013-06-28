/**
 * 
 */
package org.minnal.core.route;

import java.lang.reflect.Type;
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
	
	private String description;
	
	private String notes;
	
	private Type requestType;
	
	private Type responseType;
	
	@JsonIgnore
	private RouteConfiguration configuration;
	
	private Map<String, String> attributes = new HashMap<String, String>();
	
	private Set<QueryParam> queryParams = new HashSet<QueryParam>();
	
	/**
	 * @param routePattern
	 * @param method
	 * @param action
	 * @param requestType
	 * @param responseType
	 */
	public Route(RoutePattern routePattern, HttpMethod method, Action action,
			Type requestType, Type responseType) {
		this.routePattern = routePattern;
		this.method = method;
		this.action = action;
		this.requestType = requestType;
		this.responseType = responseType;
	}

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

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the notes
	 */
	public String getNotes() {
		return notes;
	}

	/**
	 * @return the requestType
	 */
	public Type getRequestType() {
		return requestType;
	}

	/**
	 * @return the responseType
	 */
	public Type getResponseType() {
		return responseType;
	}
	
	void setDescription(String description) {
		this.description = description;
	}
	
	void setNotes(String notes) {
		this.notes = notes;
	}
	
	void addAttribute(String key, String value) {
		this.attributes.put(key, value);
	}
	
	void setAttributes(Map<String, String> attributes) {
		this.attributes.putAll(attributes);
	}
	
	void addQueryParam(QueryParam queryParam) {
		this.queryParams.add(queryParam);
	}

	/**
	 * @param configuration the configuration to set
	 */
	void setConfiguration(RouteConfiguration configuration) {
		this.configuration = configuration;
	}
}