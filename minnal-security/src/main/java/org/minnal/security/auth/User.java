/**
 * 
 */
package org.minnal.security.auth;

import java.util.Collections;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class User implements Principal {

	private String name;
	
	private Map<String, Object> attributes;
	
	/**
	 * @param name
	 * @param attributes
	 */
	public User(String name, Map<String, Object> attributes) {
		this.name = name;
		this.attributes = attributes;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}
	
	public Object getAttribute(String attribute) {
		return attributes.get(attribute);
	}
	
}
