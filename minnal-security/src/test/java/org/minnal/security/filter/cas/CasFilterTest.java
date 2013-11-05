/**
 * 
 */
package org.minnal.security.filter.cas;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;

import java.net.URI;
import java.util.Arrays;
import java.util.Map;

import org.activejpa.entity.testng.BaseModelTest;
import org.activejpa.jpa.JPA;
import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.SeeOtherException;
import org.minnal.core.server.exception.UnauthorizedException;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;
import org.minnal.security.auth.cas.CasAuthenticator;
import org.minnal.security.auth.cas.CasCredential;
import org.minnal.security.auth.cas.CasUser;
import org.minnal.security.config.CasConfiguration;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.JpaSession;
import org.minnal.security.session.Session;
import org.minnal.security.session.SessionStore;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.internal.Maps;

/**
 * @author ganeshs
 *
 */
public class CasFilterTest extends BaseModelTest {
	
	private CasFilter filter;
	
	private SecurityConfiguration configuration;
	
	private Request request;
	
	private Response response;
	
	private SessionStore sessionStore;
	
	private FilterChain chain;
	
	private CasAuthenticator authenticator;
	
	private JpaSession session;
	
	private CasUser user;
	
	@BeforeClass
	public void beforeClass() {
		JPA.instance.addPersistenceUnit("test");
	}
	
	public void setup() throws Exception {
		super.setup();
		sessionStore = mock(SessionStore.class);
		CasConfiguration casConfiguration = new CasConfiguration("https://localhost:8443", "https://localhost:443/proxyCallback", mock(AbstractPgtTicketStorage.class), new AnyHostnameVerifier());
		configuration = new SecurityConfiguration(casConfiguration, sessionStore, 100);
		session = mock(JpaSession.class);
		user = mock(CasUser.class);
		request = mock(Request.class);
		when(request.getUri()).thenReturn(new URI("http://localhost:8080/orders"));
		when(request.getHeader("ticket")).thenReturn("test1234");
		response = mock(Response.class);
		chain = mock(FilterChain.class);
		authenticator = mock(CasAuthenticator.class);
		filter = spy(new CasFilter(configuration));
		doReturn(authenticator).when(filter).getAuthenticator();
	}
	
	@Test
	public void shouldRedirectUserToCasServerIfTicketIsMissing() {
		when(request.getHeader("ticket")).thenReturn(null);
		Session session = mock(JpaSession.class);
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(null);
		try {
			filter.doFilter(request, response, chain);
			fail("Expected SeeOther Exception but didn't get one");
		} catch (SeeOtherException e) {
		}
		
	}
	
	@Test
	public void shouldPersistCasUserToSessionOnSuccessfulAuth() {
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(session).addAttribute(Authenticator.PRINCIPAL, user);
		verify(sessionStore).save(session);
	}
	
	@Test
	public void shouldStoreSessionOnRequestOnSuccessfulAuth() {
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(request).setAttribute(Authenticator.SESSION, session);
	}
	
	@Test
	public void shouldSetCookieOnSuccessfulAuth() {
		when(session.getId()).thenReturn("test123");
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(response).addCookies(Maps.newHashMap(CasFilter.AUTH_COOKIE, session.getId()));
	}
	
	@Test
	public void shouldForwardToChainOnSuccessfulAuth() {
		when(session.getId()).thenReturn("test123");
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(request, response);
	}
	
	@Test
	public void shouldSetServiceTicketOnSuccessfulAuth() {
		when(session.getId()).thenReturn("test123");
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(session).setServiceTicket("test1234");
	}

	@Test
	public void shouldSkipAuthIfAlreadyAuthenticated() {
		when(request.getCookie(CasFilter.AUTH_COOKIE)).thenReturn("test123");
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(user);
		when(sessionStore.getSession("test123")).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(authenticator, never()).authenticate(any(CasCredential.class));
		verify(chain).doFilter(request, response);
	}
	
	@Test
	public void shouldSetSessionOnRequestIfAlreadyAuthenticated() {
		when(request.getCookie(CasFilter.AUTH_COOKIE)).thenReturn("test123");
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(user);
		when(sessionStore.getSession("test123")).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(request).setAttribute(Authenticator.SESSION, session);
	}
	
	@Test
	public void shouldRaiseUnauthorizedWhenTicketValidationFails() {
		when(session.getId()).thenReturn("test123");
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(any(CasCredential.class))).thenReturn(null);
		try {
			filter.doFilter(request, response, chain);
			fail("Expected UnAuthorized Exception but didn't get one");
		} catch (UnauthorizedException e) {
		}
	}
	
	@Test
	public void shouldAttemptReauthenticationIfSessionHasExpired() {
		when(request.getCookie(CasFilter.AUTH_COOKIE)).thenReturn("test123");
		when(sessionStore.getSession("test123")).thenReturn(session);
		when(session.hasExpired(configuration.getSessionExpiryTimeInSecs())).thenReturn(true);
		when(sessionStore.createSession(anyString())).thenReturn(session);
		try {
			filter.doFilter(request, response, chain);
			fail("Expected UnAuthorized Exception but didn't get one");
		} catch (UnauthorizedException e) {
		}
	}
	
	@Test
	public void shouldSkipAuthenticationIfUrlIsWhiteListed() throws Exception {
		configuration.setWhiteListedUrls(Arrays.asList("/skipauth"));
		when(request.getUri()).thenReturn(new URI("http://localhost:8080/skipauth/test123"));
		filter.doFilter(request, response, chain);
		verify(authenticator, never()).authenticate(any(CasCredential.class));
		verify(response, never()).addCookies(any(Map.class));
		verify(chain).doFilter(request, response);
	}
}
