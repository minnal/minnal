/**
 * 
 */
package org.minnal.security.filter;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.serializer.Serializer;
import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.utils.reflection.Generics;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;

/**
 * @author ganeshs
 *
 */
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter extends AbstractSecurityFilter implements ContainerRequestFilter {
	
	private Clients clients;
	
	public static final String PRINCIPAL = "principal";
	
	public static final String SESSION = "session";
	
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
	public void filter(ContainerRequestContext request) throws IOException {
		if (isWhiteListed(request)) {
			return;
		}
		
		Session session = getSession(request, true);
		request.setProperty(SESSION, session);
		UserProfile profile = retrieveProfile(session);
		if (profile != null) {
			return;
		}
		
		Response resp = RuntimeDelegate.getInstance().createResponseBuilder().build();
		JaxrsWebContext context = getContext(request, resp, session);
		context.getResponse().getCookies().put(AUTH_COOKIE, new NewCookie(AUTH_COOKIE, session.getId()));
		
		Client client = clients.findClient(context);
		if (client != null) {
			try {
				client.redirect(context, false, false);
			} catch (RequiresHttpAction e) {
				// TODO Log error and supress
			}
		} else {
			context.getResponse().setStatus(401);
		}
		request.abortWith(resp);
	}
	
	protected JaxrsWebContext getContext(ContainerRequestContext request, Response response, Session session) {
		ContainerResponse containerResponse = new ContainerResponse((ContainerRequest) request, response);
		return new JaxrsWebContext(request, containerResponse, session);
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
	protected UserProfile retrieveProfile(Session session) {
		Object profile = session.getAttribute(PRINCIPAL);
		if (profile == null) {
			return null;
		}
		
		Client client = getClient(session);
		Class<UserProfile> type = Generics.getTypeParameter(client.getClass(), UserProfile.class);
		if (type.isAssignableFrom(profile.getClass())) {
			return (UserProfile) profile;
		}
		if (profile instanceof Map) {
			ByteBuf buffer = Serializer.DEFAULT_JSON_SERIALIZER.serialize(profile);
			profile = Serializer.DEFAULT_JSON_SERIALIZER.deserialize(buffer, type);
			session.addAttribute(PRINCIPAL, profile);
			return (UserProfile) profile;
		}
		// Can't come here 
		return null;
	}
	
	protected Client getClient(Session session) {
		String clientName = session.getAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER);
		return clients.findClient(clientName);
	}
}
