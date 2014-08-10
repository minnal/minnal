/**
 * 
 */
package org.minnal.security;

import javax.ws.rs.core.SecurityContext;

import org.minnal.security.auth.Authorizer;
import org.minnal.security.auth.User;
import org.minnal.security.filter.AuthenticationFilter;
import org.minnal.security.session.Session;

/**
 * @author ganeshs
 *
 */
public class MinnalSecurityContext implements SecurityContext {
	
	private Authorizer authorizer;
	
	private Session session;
	
	/**
	 * @param authorizer
	 * @param session
	 */
	public MinnalSecurityContext(Authorizer authorizer, Session session) {
		this.authorizer = authorizer;
		this.session = session;
	}

	@Override
	public User getUserPrincipal() {
		return session.getAttribute(AuthenticationFilter.PRINCIPAL);
	}

	@Override
	public boolean isUserInRole(String role) {
		return authorizer.authorize(getUserPrincipal(), role);
	}

	@Override
	public boolean isSecure() {
		return false;
	}

	@Override
	public String getAuthenticationScheme() {
		return null;
	}

}
