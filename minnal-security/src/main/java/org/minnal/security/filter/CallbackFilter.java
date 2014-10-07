/**
 * 
 */
package org.minnal.security.filter;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;

import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.utils.http.HttpUtil;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
@PreMatching
@Priority(Priorities.USER)
public class CallbackFilter extends AuthenticationFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(CallbackFilter.class);
	
	/**
	 * @param clients
	 */
	public CallbackFilter(Clients clients, SecurityConfiguration configuration) {
		super(clients, configuration);
	}
	
	@Override
	public void filter(ContainerRequestContext request) {
		URI uri = URI.create(getClients().getCallbackUrl());
		if (! HttpUtil.structureUrl(request.getUriInfo().getPath()).equalsIgnoreCase(uri.getPath())) {
		    logger.debug("Request path {} doesn't match callback url. Skipping", request.getUriInfo().getPath());
			return;
		}
		
		Session session = getSession(request, true);
		JaxrsWebContext context = getContext(request, session);
		Client client = getClient(session);
		if (client == null) {
		    client = getClient(context);
		}
		if (client == null) {
			context.setResponseStatus(422);
			if (listener != null) {
			    listener.authFailed(session);
			}
		} else {
			try {
				Credentials credentials = client.getCredentials(context);
				UserProfile userProfile = client.getUserProfile(credentials, context);
				session.addAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER, client.getName());
				session.addAttribute(PRINCIPAL, userProfile);
				if (listener != null) {
	                listener.authSuccess(session, userProfile);
				}
				getConfiguration().getSessionStore().save(session);
				context.setResponseStatus(Response.Status.OK.getStatusCode());
			} catch (RequiresHttpAction e) {
				context.setResponseStatus(e.getCode());
				if (listener != null) {
	                listener.authFailed(session);
	            }
			}
		}
		request.abortWith(context.getResponse());
	}
	
	@Override
	public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
	}

}
