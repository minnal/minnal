/**
 * 
 */
package org.minnal.security.filter.basic;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.servlet.FilterChain;

import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.server.exception.UnauthorizedException;
import org.minnal.security.auth.Authenticator;
import org.minnal.security.auth.User;
import org.minnal.security.auth.basic.AbstractBasicAuthenticator;
import org.minnal.security.auth.basic.BasicCredential;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.security.session.SessionStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.BaseEncoding;

/**
 * @author ganeshs
 *
 */
public class BasicAuthenticationFilterTest {

	private BasicAuthenticationFilter filter;
	
	private AbstractBasicAuthenticator authenticator;
	
	private SecurityConfiguration configuration;
	
	private SessionStore sessionStore;
	
	private FilterChain chain;
	
	private Request request;
	
	private Response response;
	
	private Session session;
	
	private User user;
	
	@BeforeMethod
	public void setup() {
		authenticator = mock(AbstractBasicAuthenticator.class);
		configuration = new SecurityConfiguration();
		sessionStore = mock(SessionStore.class);
		configuration.setSessionStore(sessionStore);
		chain = mock(FilterChain.class);
		request = mock(Request.class);
		response = mock(Response.class);
		user = mock(User.class);
		session = mock(Session.class);
		when(request.getHeader(HttpHeaders.Names.AUTHORIZATION)).thenReturn("Basic " + BaseEncoding.base64().encode("user:password".getBytes()));
		filter = new BasicAuthenticationFilter(authenticator, configuration);
	}
	
	@Test
	public void shouldAuthenticateRequestFirstTime() {
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(null);
		when(sessionStore.createSession(anyString())).thenReturn(session);
		when(authenticator.authenticate(new BasicCredential("user", "password"))).thenReturn(user);
		filter.doFilter(request, response, chain);
		verify(sessionStore).save(session);
	}
	
	@Test(expectedExceptions=UnauthorizedException.class)
	public void shouldThrowUnAuthorizedIfAuthHeaderIsNotSet() {
		when(request.getHeader(HttpHeaders.Names.AUTHORIZATION)).thenReturn(null);
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(null);
		when(sessionStore.createSession(anyString())).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(response).setStatus(HttpResponseStatus.UNAUTHORIZED);
	}
	
	@Test(expectedExceptions=UnauthorizedException.class)
	public void shouldSetWWWAuthenticationHeaderOnAuthFailure() {
		when(request.getHeader(HttpHeaders.Names.AUTHORIZATION)).thenReturn(null);
		when(session.getAttribute(Authenticator.PRINCIPAL)).thenReturn(null);
		when(sessionStore.createSession(anyString())).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(response).addHeader(HttpHeaders.Names.WWW_AUTHENTICATE, "Realm " + configuration.getRealm());
	}
}
