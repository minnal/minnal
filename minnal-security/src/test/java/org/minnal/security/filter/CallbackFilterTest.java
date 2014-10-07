/**
 * 
 */
package org.minnal.security.filter;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.security.session.SessionStore;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.http.profile.HttpProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CallbackFilterTest {

	private CallbackFilter filter;
	
	private Clients clients;
	
	private SecurityConfiguration configuration;
	
	private ContainerRequestContext context;
	
	private UriInfo uriInfo;
	
	private SessionStore sessionStore;
	
	private AuthenticationListener listener;
	
	private Client client;
	
	@BeforeMethod
	public void setup() {
		client = mock(Client.class);
		listener = mock(AuthenticationListener.class);
		when(client.getName()).thenReturn("client1");
		clients = new Clients("/callback", client);
		sessionStore = mock(SessionStore.class);
		configuration = mock(SecurityConfiguration.class);
		when(configuration.getSessionStore()).thenReturn(sessionStore);
		filter = spy(new CallbackFilter(clients, configuration));
		filter.registerListener(listener);
		context = mock(ContainerRequestContext.class);
		uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/callback");
		when(context.getUriInfo()).thenReturn(uriInfo);
	}
	
	@Test
	public void shouldNotFilterIfRequestIsNotACallback() {
		when(uriInfo.getPath()).thenReturn("/dummy");
		filter.filter(context);
		verify(filter, never()).getSession(context, true);
	}
	
	@Test
	public void shouldReturnUnAcceptableIfClientNameNotSet() {
		Session session = mock(Session.class);
		Response response = mock(Response.class);
		JaxrsWebContext webContext = mock(JaxrsWebContext.class);
		when(webContext.getResponse()).thenReturn(response);
		doReturn(session).when(filter).getSession(context, true);
		doReturn(webContext).when(filter).getContext(context, session);
		doReturn(null).when(filter).getClient(session);
		filter.filter(context);
		verify(webContext).setResponseStatus(422);
		verify(context).abortWith(response);
		verify(listener).authFailed(session);
	}
	
	@Test
	public void shouldReturnOkIfClientNameIsSet() throws RequiresHttpAction {
		Session session = mock(Session.class);
		Response response = mock(Response.class);
		JaxrsWebContext webContext = mock(JaxrsWebContext.class);
		when(webContext.getResponse()).thenReturn(response);
		doReturn(session).when(filter).getSession(context, true);
		doReturn(webContext).when(filter).getContext(context, session);
		doReturn(client).when(filter).getClient(session);
		Credentials credentials = mock(Credentials.class);
		HttpProfile profile = mock(HttpProfile.class);
		when(client.getCredentials(webContext)).thenReturn(credentials);
		when(client.getUserProfile(credentials, webContext)).thenReturn(profile);
		filter.filter(context);
		verify(session).addAttribute(AuthenticationFilter.PRINCIPAL, profile);
		verify(session).addAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER, "client1");
		verify(sessionStore).save(session);
		verify(webContext).setResponseStatus(Response.Status.OK.getStatusCode());
		verify(listener).authSuccess(session, profile);
		verify(context).abortWith(response);
	}
}
