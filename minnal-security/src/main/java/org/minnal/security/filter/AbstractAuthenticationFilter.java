/**
 * 
 */
package org.minnal.security.filter;

import java.util.Map;
import java.util.UUID;

import org.jboss.netty.buffer.ChannelBuffer;
import org.minnal.core.Filter;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.serializer.Serializer;
import org.minnal.core.server.exception.UnauthorizedException;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.Credential;
import org.minnal.security.auth.Principal;
import org.minnal.security.auth.cas.CasAuthenticator;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.utils.reflection.Generics;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractAuthenticationFilter<C extends Credential, P extends Principal, A extends Authenticator<C, P>> implements Filter {
	
	private SecurityConfiguration configuration;
	
	public static final String AUTH_COOKIE = "_session_id";
	
	public AbstractAuthenticationFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void doFilter(Request request, Response response, FilterChain chain) {
		boolean whiteListed = isWhiteListed(request);
		boolean alreadyAuthenticated = false;
		
		Session session = null;
		if (! whiteListed) {
			session = getSession(request, true);
			request.setAttribute(Authenticator.SESSION, session);
			
			P principal = retrievePrincipal(session);
			if (principal != null) {
				alreadyAuthenticated = true;
				session.addAttribute(Authenticator.PRINCIPAL, principal);
			}
			
			if (! alreadyAuthenticated) {
				principal = getAuthenticator().authenticate(getCredential(request));
				if (principal == null) {
					handleAuthFailure(request, response, session);
				} else {
					session.addAttribute(Authenticator.PRINCIPAL, principal);
					handleAuthSuccess(request, response, session);
				}
			}
		}
		
		chain.doFilter(request, response);
		
		if (! whiteListed) {
			if (! alreadyAuthenticated) {
				Map<String, String> map = Maps.newHashMap();
				map.put(AUTH_COOKIE, session.getId());
				response.addCookies(map);
			}
		}
	}
	
	protected boolean isWhiteListed(Request request) {
		for (String url : configuration.getWhiteListedUrls()) {
			if (request.getUri().getPath().startsWith(url)) {
				return true;
			}
		}
		return false;
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
	
	@SuppressWarnings("unchecked")
	protected P retrievePrincipal(Session session) {
		Object principal = session.getAttribute(Authenticator.PRINCIPAL);
		if (principal == null) {
			return null;
		}
		
		Class<P> type = Generics.getTypeParameter(getAuthenticator().getClass(), Principal.class);
		if (type.isAssignableFrom(principal.getClass())) {
			return (P) principal;
		}
		if (principal instanceof Map) {
			ChannelBuffer buffer = Serializer.DEFAULT_JSON_SERIALIZER.serialize(principal);
			principal = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(buffer, type);
			session.addAttribute(Authenticator.PRINCIPAL, principal);
			return (P) principal;
		}
		// Can't come here 
		return null;
	}

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
