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
				throw new UnauthorizedException();
			}
			session.addAttribute(PRINCIPAL, principal);
			configuration.getSessionStore().save(session);
		}
		chain.doFilter(request, response);
		
		if (! alreadyAuthenticated) {
			Map<String, String> map = Maps.newHashMap();
			map.put(AUTH_COOKIE, session.getId());
			response.addCookies(map);
		}
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
}
