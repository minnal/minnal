/**
 * 
 */
package org.minnal.security.filter;

import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;

import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;

import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class AbstractSecurityFilter {
	
	private SecurityConfiguration configuration;
	
	public static final String AUTH_COOKIE = "_session_id";
	
	public static final String SESSION = "session";
	
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
		Session session = (Session) request.getProperty(SESSION);
		if (session != null) {
		    return session;
		}
		Cookie sessionCookie = request.getCookies().get(AUTH_COOKIE);
		
		if (sessionCookie != null) {
			session = configuration.getSessionStore().getSession(sessionCookie.getValue());
		}
		
		if (session != null && session.hasExpired(configuration.getSessionExpiryTimeInSecs())) {
			session = null;
		}
		if (session == null && create) {
			String sessionId = null;
			if (Strings.isNullOrEmpty(sessionId)) {
				sessionId = UUID.randomUUID().toString();
			}
			session = configuration.getSessionStore().createSession(sessionId);
		}
		return session;
	}
}
