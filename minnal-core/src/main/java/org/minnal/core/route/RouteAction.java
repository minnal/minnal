/**
 * 
 */
package org.minnal.core.route;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @author ganeshs
 *
 */
public class RouteAction {

	private Route route;

	/**
	 * @param route
	 */
	public RouteAction(Route route) {
		this.route = route;
	}

	public void description(String description) {
		route.setDescription(description);
	}
	
	public void notes(String notes) {
		route.setNotes(notes);
	}
	
	public RouteAction queryParam(QueryParam param) {
		route.addQueryParam(param);
		return this;
	}
	
	public RouteAction queryParam(String name) {
		return queryParam(name, "");
	}
	
	public RouteAction queryParam(String name, String description) {
		return queryParam(name, QueryParam.Type.string, description);
	}
	
	public RouteAction queryParam(String name, QueryParam.Type type, String description) {
		return queryParam(new QueryParam(name, type, description));
	}

	Route getRoute() {
		return route;
	}
	
	public RouteAction attributes(Map<String, String> attributes) {
		route.setAttributes(attributes);
		return this;
	}
	
	public RouteAction attribute(String key, String value) {
		route.addAttribute(key, value);
		return this;
	}
	
	void addAttributes(Map<String, String> attributes, boolean overwrite) {
		if (overwrite) {
			attributes(attributes);
		} else {
			for (Entry<String, String> entry : attributes.entrySet()) {
				if (! route.getAttributes().containsKey(entry.getKey())) {
					attribute(entry.getKey(), entry.getValue());
				}
			}
		}
	}
}
