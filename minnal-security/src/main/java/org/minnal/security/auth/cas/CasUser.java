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
	
	private AttributePrincipal principal;

	/**
	 * @param principal
	 */
	public CasUser(AttributePrincipal principal) {
		super(principal.getName(), principal.getAttributes());
		this.principal = principal;
	}

	/**
	 * @return the principal
	 */
	public AttributePrincipal getPrincipal() {
		return principal;
	}
}
