/**
 * 
 */
package org.minnal.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.Authorizer;
import org.minnal.security.auth.Permission;
import org.minnal.security.session.Session;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class MinnalSecurityContext implements SecurityContext {
	
	private Authorizer authorizer;
	
	private Session session;
	
	/**
	 * @return the authorizer
	 */
	public Authorizer getAuthorizer() {
		return authorizer;
	}

	/**
	 * @param authorizer the authorizer to set
	 */
	public void setAuthorizer(Authorizer authorizer) {
		this.authorizer = authorizer;
	}

	@Override
	public Principal getUserPrincipal() {
		return session.getAttribute(Authenticator.PRINCIPAL);
	}

	@Override
	public boolean isUserInRole(String role) {
		return authorizer.authorize(getUserPrincipal(), Lists.newArrayList(new Permission(role)));
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
