/**
 * 
 */
package org.minnal.security.filter;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.servlet.FilterChain;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;

import org.minnal.core.serializer.Serializer;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.Credential;
import org.minnal.security.auth.Principal;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.utils.reflection.Generics;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractAuthenticationFilter<C extends Credential, P extends Principal, A extends Authenticator<C, P>> implements ContainerRequestFilter, ContainerResponseFilter {
	
	private SecurityConfiguration configuration;
	
	public static final String AUTH_COOKIE = "_session_id";
	
	public AbstractAuthenticationFilter(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}
	
	@Override
	public void filter(ContainerRequestContext request) throws IOException {
		boolean whiteListed = isWhiteListed(request);
		boolean alreadyAuthenticated = false;
		
		Session session = null;
		if (! whiteListed) {
			session = getSession(request, true);
			request.setProperty(Authenticator.SESSION, session);
			
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
	}
	
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
		if (! whiteListed) {
			if (! alreadyAuthenticated) {
				Map<String, String> map = Maps.newHashMap();
				map.put(AUTH_COOKIE, session.getId());
				response.addCookies(map);
			}
		}
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
	
	protected boolean isWhiteListed(ContainerRequestContext request) {
		for (String url : configuration.getWhiteListedUrls()) {
			if (request.getUriInfo().getPath().startsWith(url)) {
				return true;
			}
		}
		return false;
	}
	
	protected void handleAuthSuccess(ContainerRequestContext request, ContainerResponseContext response, Session session) {
		configuration.getSessionStore().save(session);
	}
	
	protected void handleAuthFailure(ContainerRequestContext request, ContainerResponseContext response, Session session) {
		throw new NotAuthorizedException(response.);
	}

	protected abstract C getCredential(ContainerRequestContext request);
	
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
			ByteBuf buffer = Serializer.DEFAULT_JSON_SERIALIZER.serialize(principal);
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
