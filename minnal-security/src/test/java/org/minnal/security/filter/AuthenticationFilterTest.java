/**
 * 
 */
package org.minnal.security.filter;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.minnal.security.auth.JaxrsWebContext;
import org.minnal.security.auth.User;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.security.session.SessionStore;
import org.pac4j.core.client.Clients;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.http.client.BasicAuthClient;
import org.pac4j.http.profile.HttpProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author ganeshs
 *
 */
public class AuthenticationFilterTest {

	private AuthenticationFilter filter;
	
	private Clients clients;
	
	private SecurityConfiguration configuration;
	
	private ContainerRequestContext context;
	
	private UriInfo uriInfo;
	
	private SessionStore sessionStore;
	
	private BasicAuthClient basicClient;
	
	@BeforeMethod
	public void setup() {
		basicClient = mock(BasicAuthClient.class);
		when(basicClient.getName()).thenReturn("client1");
		clients = new Clients("/callback", basicClient);
		sessionStore = mock(SessionStore.class);
		configuration = mock(SecurityConfiguration.class);
		when(configuration.getSessionStore()).thenReturn(sessionStore);
		filter = spy(new AuthenticationFilter(clients, configuration));
		context = mock(ContainerRequestContext.class);
		uriInfo = mock(UriInfo.class);
		when(uriInfo.getPath()).thenReturn("/dummy");
		when(context.getUriInfo()).thenReturn(uriInfo);
	}
	
	@Test
	public void shouldReturnTrueIfWhiteListedUrl() {
		when(configuration.getWhiteListedUrls()).thenReturn(Lists.newArrayList("/dummy", "/dummy1"));
		assertTrue(filter.isWhiteListed(context));
	}
	
	@Test
	public void shouldReturnFalseIfWhiteNotListedUrl() {
		when(configuration.getWhiteListedUrls()).thenReturn(Lists.newArrayList("/dummy2", "/dummy1"));
		assertFalse(filter.isWhiteListed(context));
	}
	
	@Test
	public void shouldReturnTrueForWhiteListedUrlWithParams() {
		when(configuration.getWhiteListedUrls()).thenReturn(Lists.newArrayList("/dummy", "/dummy1"));
		when(uriInfo.getPath()).thenReturn("/dummy?key=value");
		assertTrue(filter.isWhiteListed(context));
	}
	
	@Test
	public void shouldCreateSessionIfAuthCookieIsNotFound() {
		when(context.getCookies()).thenReturn(Maps.<String, Cookie>newHashMap());
		Session session = mock(Session.class);
		when(sessionStore.createSession(any(String.class))).thenReturn(session);
		assertEquals(filter.getSession(context, true), session);
	}
	
	@Test
	public void shouldNotCreateSessionIfAuthCookieIsNotFoundAndCreateIsFalse() {
		when(context.getCookies()).thenReturn(Maps.<String, Cookie>newHashMap());
		Session session = mock(Session.class);
		when(sessionStore.createSession(any(String.class))).thenReturn(session);
		assertNull(filter.getSession(context, false));
	}
	
	@Test
	public void shouldCreateSessionIfAuthCookieIsFoundButSessionNotFound() {
		Map<String, Cookie> cookies = new HashMap<String, Cookie>();
		String sessionId = UUID.randomUUID().toString();
		cookies.put(AuthenticationFilter.AUTH_COOKIE, new Cookie(AuthenticationFilter.AUTH_COOKIE, sessionId));
		when(context.getCookies()).thenReturn(cookies);
		Session session = mock(Session.class);
		when(sessionStore.getSession(sessionId)).thenReturn(null);
		when(sessionStore.createSession(any(String.class))).thenReturn(session);
		assertEquals(filter.getSession(context, true), session);
		verify(sessionStore).getSession(sessionId);
	}
	
	@Test
	public void shouldCreateSessionIfAuthCookieIsFoundButSessionHasExpired() {
		when(configuration.getSessionExpiryTimeInSecs()).thenReturn(100L);
		Map<String, Cookie> cookies = new HashMap<String, Cookie>();
		String sessionId = UUID.randomUUID().toString();
		cookies.put(AuthenticationFilter.AUTH_COOKIE, new Cookie(AuthenticationFilter.AUTH_COOKIE, sessionId));
		when(context.getCookies()).thenReturn(cookies);
		Session expiredSession = mock(Session.class);
		Session session = mock(Session.class);
		when(expiredSession.hasExpired(100)).thenReturn(true);
		when(sessionStore.getSession(sessionId)).thenReturn(expiredSession);
		
		when(sessionStore.createSession(any(String.class))).thenReturn(session);
		assertEquals(filter.getSession(context, true), session);
		verify(sessionStore).getSession(sessionId);
	}
	
	@Test
	public void shouldReturnSessionIfAuthCookieIsFoundAndSessionHasNotExpired() {
		when(configuration.getSessionExpiryTimeInSecs()).thenReturn(100L);
		Map<String, Cookie> cookies = new HashMap<String, Cookie>();
		String sessionId = UUID.randomUUID().toString();
		cookies.put(AuthenticationFilter.AUTH_COOKIE, new Cookie(AuthenticationFilter.AUTH_COOKIE, sessionId));
		when(context.getCookies()).thenReturn(cookies);
		Session session = mock(Session.class);
		when(session.hasExpired(100)).thenReturn(false);
		when(sessionStore.getSession(sessionId)).thenReturn(session);
		assertEquals(filter.getSession(context, true), session);
		verify(sessionStore, never()).createSession(any(String.class));
	}
	
	@Test
	public void shouldReturnNullClientFromRequestContextIfClientNameAttributeIsNotSet() {
		JaxrsWebContext context = mock(JaxrsWebContext.class);
		when(context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn(null);
		assertNull(filter.getClient(context));
	}
	
	@Test(expectedExceptions=TechnicalException.class)
	public void shouldThrowExceptionIfClientNameIsNotFoundInRequestContext() {
		JaxrsWebContext context = mock(JaxrsWebContext.class);
		when(context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn("unknownClient");
		filter.getClient(context);
	}
	
	@Test
	public void shouldGetClientFromRequestContextIfClientNameAttributeIsSet() {
		JaxrsWebContext context = mock(JaxrsWebContext.class);
		when(context.getRequestParameter(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn("client1");
		assertEquals(filter.getClient(context), basicClient);
	}
	
	@Test
	public void shouldGetClientFromSessionIfClientNameAttributeIsSet() {
		Session session = mock(Session.class);
		when(session.getAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn("client1");
		assertEquals(filter.getClient(session), basicClient);
	}
	
	@Test
	public void shouldReturnNullClientFromSessionIfClientNameAttributeIsNotSet() {
		Session session = mock(Session.class);
		when(session.getAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn(null);
		assertNull(filter.getClient(session));
	}
	
	@Test(expectedExceptions=TechnicalException.class)
	public void shouldThrowExceptionIfClientNameIsNotFoundInSession() {
		Session session = mock(Session.class);
		when(session.getAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER)).thenReturn("unknownClient");
		filter.getClient(session);
	}
	
	@Test
	public void shouldReturnNullProfileIfNotFoundInSession() {
		Session session = mock(Session.class);
		when(session.getAttribute(AuthenticationFilter.PRINCIPAL)).thenReturn(null);
		assertNull(filter.retrieveProfile(session));
	}
	
	@Test
	public void shouldReturnProfileIfFoundInSession() {
		Session session = mock(Session.class);
		HttpProfile profile = mock(HttpProfile.class);
		when(session.getAttribute(AuthenticationFilter.PRINCIPAL)).thenReturn(profile);
		doReturn(basicClient).when(filter).getClient(session);
		
		User user = filter.retrieveProfile(session);
		assertEquals(user.getProfile(), profile);
	}
	
	@Test
	public void shouldDeserializeProfileIfFoundInSessionAsMap() {
		Session session = mock(Session.class);
		Map<String, Object> profile = new HashMap<String, Object>();
		profile.put("id", "dummy");
		when(session.getAttribute(AuthenticationFilter.PRINCIPAL)).thenReturn(profile);
		doReturn(basicClient).when(filter).getClient(session);
		User user = filter.retrieveProfile(session);
		assertEquals(user.getProfile().getId(), "dummy");
	}
	
	@Test
	public void shouldReturnTrueIfAlreadyAuthenticated() {
		Session session = mock(Session.class);
		doReturn(mock(User.class)).when(filter).retrieveProfile(session);
		assertTrue(filter.isAuthenticated(session));
	}
	
	@Test
	public void shouldReturnFalseIfNotAlreadyAuthenticated() {
		Session session = mock(Session.class);
		doReturn(null).when(filter).retrieveProfile(session);
		assertFalse(filter.isAuthenticated(session));
	}

	@Test
	public void shouldNotFilterWhiteListedUrls() {
		doReturn(true).when(filter).isWhiteListed(context);
		filter.filter(context);
		verify(filter, never()).getSession(context, true);
	}
	
	@Test
	public void shouldFilterIfAlreadyAuthenticated() {
		Session session = mock(Session.class);
		doReturn(true).when(filter).isAuthenticated(session);
		doReturn(session).when(filter).getSession(context, true);
		filter.filter(context);
		verify(context, never()).abortWith(any(Response.class));
	}

	@Test
	public void shouldReturnUnauthorizedIfClientNameIsNotSet() {
		Session session = mock(Session.class);
		Response response = mock(Response.class);
		JaxrsWebContext webContext = mock(JaxrsWebContext.class);
		when(webContext.getResponse()).thenReturn(response);
		doReturn(false).when(filter).isAuthenticated(session);
		doReturn(session).when(filter).getSession(context, true);
		doReturn(webContext).when(filter).getContext(context, session);
		filter.filter(context);
		verify(webContext).setResponseStatus(Response.Status.UNAUTHORIZED.getStatusCode());
		verify(context).abortWith(response);
	}
	
	@Test
	public void shouldSetClientNameParamInSessionIfNotAuthenticated() throws RequiresHttpAction {
		Session session = mock(Session.class);
		Response response = mock(Response.class);
		JaxrsWebContext webContext = mock(JaxrsWebContext.class);
		when(webContext.getResponse()).thenReturn(response);
		doReturn(basicClient).when(filter).getClient(webContext);
		doReturn(false).when(filter).isAuthenticated(session);
		doReturn(session).when(filter).getSession(context, true);
		doReturn(webContext).when(filter).getContext(context, session);
		filter.filter(context);
		verify(session).addAttribute(Clients.DEFAULT_CLIENT_NAME_PARAMETER, "client1");
		verify(sessionStore).save(session);
	}
	
	@Test
	public void shouldRedirectIfClientNameIsSet() throws RequiresHttpAction {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn(UUID.randomUUID().toString());
		Response response = mock(Response.class);
		JaxrsWebContext webContext = mock(JaxrsWebContext.class);
		when(webContext.getResponse()).thenReturn(response);
		doNothing().when(basicClient).redirect(webContext, false, false);
		doReturn(basicClient).when(filter).getClient(webContext);
		doReturn(false).when(filter).isAuthenticated(session);
		doReturn(session).when(filter).getSession(context, true);
		doReturn(webContext).when(filter).getContext(context, session);
		filter.filter(context);
		verify(basicClient).redirect(webContext, false, false);
		verify(webContext, atLeast(1)).setResponseHeader(eq(HttpHeaders.LOCATION), any(String.class));
		verify(webContext).setResponseHeader(HttpHeaders.SET_COOKIE, new NewCookie(AuthenticationFilter.AUTH_COOKIE, session.getId()).toString());
		verify(context).abortWith(response);
	}
}
