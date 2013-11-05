/**
 * 
 */
package org.minnal.security.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class User implements Principal {

	private String name;
	
	private Map<String, Object> attributes;
	
	private List<Role> roles;
	
	private List<Permission> permissions;
	
	public User() {
	}
	
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
	
	public List<Role> getRoles() {
		return roles;
	}
	
	@Override
	public List<Permission> getPermissions() {
		return permissions;
	}
	
	/**
	 * Checks if the user has all the given roles
	 * 
	 * @param permissions
	 * @return
	 */
	public boolean hasPermissions(List<Permission> permissions) {
		if (permissions == null) {
			return false;
		}
		return this.permissions.containsAll(permissions);
	}
	
	/**
	 * Checks if the user has the given role
	 * 
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(Permission permission) {
		if (permissions == null) {
			return false;
		}
		return this.roles.contains(permission);
	}

	/**
	 * @param name the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @param attributes the attributes to set
	 */
	void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param roles the roles to set
	 */
	void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	/**
	 * @param permissions the permissions to set
	 */
	void setPermissions(List<Permission> permissions) {
		this.permissions = permissions;
	}
}
