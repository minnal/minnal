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
	 * Authorize the user against the given permissions
	 * 
	 * @param user
	 * @param permissions
	 * @return
	 */
	boolean authorize(User user, List<String> permissions);
	
	/**
	 * Authorize the user against the given permission
	 * 
	 * @param user
	 * @param permission
	 * @return
	 */
	boolean authorize(User user, String permission);
}
