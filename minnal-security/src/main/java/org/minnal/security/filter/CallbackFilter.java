/**
 * 
 */
package org.minnal.security.filter;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

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
	public void filter(ContainerRequestContext request) throws IOException {
		URI uri = URI.create(getClients().getCallbackUrl());
		if (! request.getUriInfo().getRequestUri().getPath().equalsIgnoreCase(uri.getPath())) {
			return;
		}
		Session session = getSession(request, true);
		Client client = getClient(session);
		
		Response resp = RuntimeDelegate.getInstance().createResponseBuilder().build();
		JaxrsWebContext context = getContext(request, resp, session);
		try {
			Credentials credentials = client.getCredentials(context);
			UserProfile userProfile = client.getUserProfile(credentials, context);
			session.addAttribute(PRINCIPAL, userProfile);
			context.getResponse().setStatusInfo(Response.Status.OK);
		} catch (RequiresHttpAction e) {
			context.getResponse().setStatus(e.getCode());
		}
		request.abortWith(resp);
	}

}
