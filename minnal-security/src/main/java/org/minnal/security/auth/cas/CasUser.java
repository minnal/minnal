/**
 * 
 */
package org.minnal.security.auth.cas;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.minnal.security.auth.User;

/**
 * @author ganeshs
 *
 */
public class CasUser extends User {
	
	public CasUser() {
	}

	/**
	 * @param principal
	 */
	public CasUser(AttributePrincipal principal) {
		super(principal.getName(), principal.getAttributes());
	}
}
