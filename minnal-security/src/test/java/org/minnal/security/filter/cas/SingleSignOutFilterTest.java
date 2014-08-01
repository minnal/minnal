/**
 * 
 */
package org.minnal.security.filter.cas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.fail;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.servlet.FilterChain;

import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.core.MinnalException;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;
import org.minnal.security.config.CasConfiguration;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.Session;
import org.minnal.security.session.SessionStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class SingleSignOutFilterTest {
	
	private SingleSignOutFilter filter;
	
	private SecurityConfiguration configuration;
	
	private SessionStore sessionStore;
	
	private Request request;
	
	private Response response;
	
	private FilterChain chain;
	
	private AbstractPgtTicketStorage storage;

	@BeforeMethod
	public void setup() {
		sessionStore = mock(SessionStore.class);
		storage = mock(AbstractPgtTicketStorage.class);
		CasConfiguration casConfiguration = new CasConfiguration("https://localhost:8443", "https://localhost:443/proxyCallback", storage, new AnyHostnameVerifier());
		configuration = new SecurityConfiguration(casConfiguration, sessionStore, 100);
		filter = new SingleSignOutFilter(configuration);
		request = mock(Request.class);
		when(request.getHeader("logoutRequest")).thenReturn("<samlp:LogoutRequest><samlp:SessionIndex>Test123</samlp:SessionIndex></samlp:LogoutRequest>");
		when(request.getHttpMethod()).thenReturn(HttpMethod.POST);
		response = mock(Response.class);
		chain = mock(FilterChain.class);
	}
	
	@Test
	public void shouldNotProcessRequestIfRequestMethodIsNotPost() {
		when(request.getHttpMethod()).thenReturn(HttpMethod.GET);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(request, response);
	}
	
	@Test
	public void shouldNotProcessRequestIfHeaderLogoutRequestIsNotFound() {
		when(request.getHeader("logoutRequest")).thenReturn(null);
		filter.doFilter(request, response, chain);
		verify(chain).doFilter(request, response);
	}
	
	@Test
	public void shouldDestroySessionOnLogoutRequest() {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("123");
		when(sessionStore.findSessionBy("serviceTicket", "Test123")).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(sessionStore).deleteSession("123");
		verify(chain, never()).doFilter(request, response);
		verify(response).setStatus(HttpResponseStatus.OK);
	}
	
	@Test
	public void shouldNotDestroySessionIfSingleSignOutIsDisabled() {
		configuration.getCasConfiguration().setEnableSingleSignout(false);
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("123");
		when(sessionStore.findSessionBy("serviceTicket", "Test123")).thenReturn(session);
		filter.doFilter(request, response, chain);
		verify(sessionStore, never()).deleteSession("123");
		verify(chain, never()).doFilter(request, response);
		verify(response).setStatus(HttpResponseStatus.OK);
	}
	
	@Test
	public void shouldThrowMinnalExceptionOnBadLogoutRequest() {
		when(request.getHeader("logoutRequest")).thenReturn("test123");
		try {
			filter.doFilter(request, response, chain);
			fail("Expected Minnal Exception but got none");
		} catch (MinnalException e) {
		}
	}
	
	@Test
	public void shouldRespondOkEvenWhenSessionIsNotFound() {
		Session session = mock(Session.class);
		when(session.getId()).thenReturn("123");
		when(sessionStore.findSessionBy("serviceTicket", "Test123")).thenReturn(null);
		filter.doFilter(request, response, chain);
		verify(sessionStore, never()).deleteSession("123");
		verify(chain, never()).doFilter(request, response);
		verify(response).setStatus(HttpResponseStatus.OK);
	}
}
