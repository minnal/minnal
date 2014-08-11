/**
 * 
 */
package org.minnal.security.filter;

import java.net.URI;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;

/**
 * @author ganeshs
 *
 */
@PreMatching
@Priority(Priorities.USER)
public class CallbackFilter extends AuthenticationFilter {
	
	/**
	 * @param clients
	 */
	public CallbackFilter(Clients clients, SecurityConfiguration configuration) {
		super(clients, configuration);
	}

	@Override
	public void filter(ContainerRequestContext request) {
		URI uri = URI.create(getClients().getCallbackUrl());
		if (! request.getUriInfo().getPath().equalsIgnoreCase(uri.getPath())) {
			return;
		}
		Session session = getSession(request, true);
		Client client = getClient(session);
		JaxrsWebContext context = getContext(request, session);
		if (client == null) {
			context.setResponseStatus(422);
		} else {
			try {
				Credentials credentials = client.getCredentials(context);
				UserProfile userProfile = client.getUserProfile(credentials, context);
				session.addAttribute(PRINCIPAL, userProfile);
				getConfiguration().getSessionStore().save(session);
				context.setResponseStatus(Response.Status.OK.getStatusCode());
			} catch (RequiresHttpAction e) {
				context.setResponseStatus(e.getCode());
			}
		}
		request.abortWith(context.getResponse());
	}

}
