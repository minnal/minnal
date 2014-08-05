/**
 * 
 */
package org.minnal.security.auth;

import java.util.List;

/**
 * @author ganeshs
 *
 */
public interface Authorizer {

	public static final String PERMISSIONS = "permissions";

	/**
	 * Authorize the user against the given roles
	 * 
	 * @param principal
	 * @param permissions
	 * @return
	 */
	boolean authorize(java.security.Principal principal, List<Permission> permissions);
}
