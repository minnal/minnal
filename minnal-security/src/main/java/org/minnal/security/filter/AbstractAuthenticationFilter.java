/**
 * 
 */
package org.minnal.security.filter;

import java.util.Map;
import java.util.UUID;

import org.minnal.core.Filter;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.UnauthorizedException;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.Credential;
import org.minnal.security.auth.Principal;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractAuthenticationFilter<C extends Credential, P extends Principal, A extends Authenticator<C, P>> implements Filter {
	
	private SecurityConfiguration configuration;
	
	public static final String AUTH_COOKIE = "_session_id";
	
	public static final String PRINCIPAL = "principal";
	
	public AbstractAuthenticationFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void doFilter(Request request, Response response, FilterChain chain) {
		Session session = getSession(request, true);
		boolean alreadyAuthenticated = false;
		if (session.containsAttribute(PRINCIPAL)) {
			alreadyAuthenticated = true;
		}
		
		if (! alreadyAuthenticated) {
			P principal = getAuthenticator().authenticate(getCredential(request));
			if (principal == null) {
				handleAuthFailure(request, response, session);
			} else {
				session.addAttribute(PRINCIPAL, principal);
				handleAuthSuccess(request, response, session);
			}
		}
		chain.doFilter(request, response);
		
		if (! alreadyAuthenticated) {
			Map<String, String> map = Maps.newHashMap();
			map.put(AUTH_COOKIE, session.getId());
			response.addCookies(map);
		}
	}
	
	protected void handleAuthSuccess(Request request, Response response, Session session) {
		configuration.getSessionStore().save(session);
	}
	
	protected void handleAuthFailure(Request request, Response response, Session session) {
		throw new UnauthorizedException();
	}

	protected abstract C getCredential(Request request);
	
	protected Session getSession(Request request, boolean create) {
		Session session = null;
		String sessionCookie = request.getCookie(AUTH_COOKIE);
		if (Strings.isNullOrEmpty(sessionCookie)) {
			sessionCookie = UUID.randomUUID().toString();
		} else {
			session = configuration.getSessionStore().getSession(sessionCookie);
		}
		
		if (session != null && session.hasExpired(configuration.getSessionExpiryTimeInSecs())) {
			session = null;
			sessionCookie = UUID.randomUUID().toString();
		}
		if (session == null && create) {
			session = configuration.getSessionStore().createSession(sessionCookie);
		}
		return session;
	}
	
	protected SecurityConfiguration getConfiguration() {
		return configuration;
	}
	
	protected abstract A getAuthenticator();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((configuration == null) ? 0 : configuration.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractAuthenticationFilter other = (AbstractAuthenticationFilter) obj;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		return true;
	}
}
