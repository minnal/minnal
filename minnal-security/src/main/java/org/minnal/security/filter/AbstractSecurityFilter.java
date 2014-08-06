/**
 * 
 */
package org.minnal.security.filter;

import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;

import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;

/**
 * @author ganeshs
 *
 */
public class AbstractSecurityFilter {
	
	private SecurityConfiguration configuration;
	
	public static final String AUTH_COOKIE = "_session_id";
	
	/**
	 * @param configuration
	 */
	public AbstractSecurityFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * @return the configuration
	 */
	public SecurityConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param request
	 * @param create
	 * @return
	 */
	protected Session getSession(ContainerRequestContext request, boolean create) {
		Session session = null;
		Cookie sessionCookie = request.getCookies().get(AUTH_COOKIE);
		String sessionId = null;
		if (sessionCookie == null) {
			sessionId = UUID.randomUUID().toString();
		} else {
			session = configuration.getSessionStore().getSession(sessionCookie.getValue());
		}
		
		if (session != null && session.hasExpired(configuration.getSessionExpiryTimeInSecs())) {
			session = null;
			sessionId = UUID.randomUUID().toString();
		}
		if (session == null && create) {
			session = configuration.getSessionStore().createSession(sessionId);
		}
		return session;
	}
}
