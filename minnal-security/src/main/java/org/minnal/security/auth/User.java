/**
 * 
 */
package org.minnal.security.auth;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.pac4j.core.profile.UserProfile;

/**
 * @author ganeshs
 *
 */
public class User implements Principal {
	
	private UserProfile profile;

	public User() {
	}
	
	/**
	 * @param profile
	 */
	public User(UserProfile profile) {
		this.profile = profile;
	}

	/**
	 * @return the profile
	 */
	public UserProfile getProfile() {
		return profile;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return profile.getId();
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return profile.getAttributes();
	}
	
	public Object getAttribute(String attribute) {
		return profile.getAttribute(attribute);
	}
	
	public List<String> getRoles() {
		return profile.getRoles();
	}
	
	public List<String> getPermissions() {
		return profile.getPermissions();
	}
	
	/**
	 * Checks if the user has all the given roles
	 * 
	 * @param permissions
	 * @return
	 */
	public boolean hasPermissions(List<String> permissions) {
		if (permissions == null) {
			return false;
		}
		return getPermissions().containsAll(permissions);
	}
	
	/**
	 * Checks if the user has the given role
	 * 
	 * @param permission
	 * @return
	 */
	public boolean hasPermission(String permission) {
		if (getPermissions() == null) {
			return false;
		}
		return getPermissions().contains(permission);
	}

	/**
	 * @param roles the roles to set
	 */
	void setRoles(List<String> roles) {
		for (String role : roles) {
			profile.addRole(role);
		}
	}

	/**
	 * @param permissions the permissions to set
	 */
	void setPermissions(List<String> permissions) {
		for (String permission : permissions) {
			profile.addPermission(permission);
		}
	}
}
