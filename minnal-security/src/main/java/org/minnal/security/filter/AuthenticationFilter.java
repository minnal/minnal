/**
 * 
 */
package org.minnal.security.filter;

import io.netty.buffer.ByteBuf;

import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.minnal.core.serializer.Serializer;
import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.auth.User;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.utils.reflection.Generics;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter extends AbstractSecurityFilter implements ContainerRequestFilter {
	
	private Clients clients;
	
	public static final String PRINCIPAL = "principal";
	
	public static final String SESSION = "session";
	
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
	
	/**
	 * @param clients
	 * @param configuration
	 */
	public AuthenticationFilter(Clients clients, SecurityConfiguration configuration) {
		super(configuration);
		this.clients = clients;
	}
	
	/**
	 * @return the clients
	 */
	public Clients getClients() {
		return clients;
	}

	@Override
	public void filter(ContainerRequestContext request) {
		if (isWhiteListed(request)) {
			return;
		}
		
		Session session = getSession(request, true);
		request.setProperty(SESSION, session);
		if (isAuthenticated(session)) {
			return;
		}
		
		JaxrsWebContext context = getContext(request, session);
		
		Client client = null;
		try {
			client = getClient(context);
		} catch (TechnicalException e) {
			logger.error("Failed while getiing the client", e);
		}
		
		if (client != null) {
			session.addAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER, client.getName());
			getConfiguration().getSessionStore().save(session);
			
			try {
				client.redirect(context, false, false);
			} catch (RequiresHttpAction e) {
				logger.error("Failed while redirecting the request", e);
				context.setResponseStatus(e.getCode());
			}
		} else {
			context.setResponseStatus(Response.Status.UNAUTHORIZED.getStatusCode());
		}
		context.setResponseHeader(HttpHeaders.SET_COOKIE, new NewCookie(AUTH_COOKIE, session.getId()).toString());
		request.abortWith(context.getResponse());
	}
	
	/**
	 * Checks if the session is already authenticated
	 * 
	 * @param session
	 * @return
	 */
	protected boolean isAuthenticated(Session session) {
		return retrieveProfile(session) != null;
	}
	
	protected JaxrsWebContext getContext(ContainerRequestContext request, Session session) {
		return new JaxrsWebContext(request, new OutboundMessageContext(), session);
	}
	
	protected boolean isWhiteListed(ContainerRequestContext request) {
		for (String url : getConfiguration().getWhiteListedUrls()) {
			if (request.getUriInfo().getPath().startsWith(url)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	protected User retrieveProfile(Session session) {
		Object profile = session.getAttribute(PRINCIPAL);
		if (profile == null) {
			return null;
		}
		
		Client client = getClient(session);
		Class<UserProfile> type = Generics.getTypeParameter(client.getClass(), UserProfile.class);
		if (type.isAssignableFrom(profile.getClass())) {
			return new User((UserProfile) profile);
		}
		if (profile instanceof Map) {
			ByteBuf buffer = Serializer.DEFAULT_JSON_SERIALIZER.serialize(profile);
			profile = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(buffer, type);
			User user = new User((UserProfile) profile);
			session.addAttribute(PRINCIPAL, profile);
			return user;
		}
		// Can't come here 
		return null;
	}
	
	protected Client getClient(Session session) {
		String clientName = session.getAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER);
		if (Strings.isNullOrEmpty(clientName)) {
			return null;
		}
		return clients.findClient(clientName);
	}
	
	protected Client getClient(JaxrsWebContext context) {
		String clientName = context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER);
		if (Strings.isNullOrEmpty(clientName)) {
			return null;
		}
		return clients.findClient(clientName);
	}
}
