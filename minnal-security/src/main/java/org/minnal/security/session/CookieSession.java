/**
 * 
 */
package org.minnal.security.session;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class CookieSession implements Session {

	private static final long serialVersionUID = 1L;
	
	private String id;
	
	private Map<String, Object> attributes = new HashMap<String, Object>();
	
	private Timestamp createdAt;
	
	/**
	 * @param id
	 */
	public CookieSession(String id) {
		this.id = id;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return Collections.unmodifiableMap(attributes);
	}
	
	public void addAttribute(String name, Object value) {
		attributes.put(name, value);
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String name) {
		return (T) attributes.get(name);
	}
	
	public boolean containsAttribute(String name) {
		return attributes.containsKey(name);
	}

	/**
	 * @return the createdAt
	 */
	public Timestamp getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public boolean hasExpired(long timeoutInSecs) {
		return new Timestamp(System.currentTimeMillis() - timeoutInSecs * 1000).before(createdAt);
	}
}
