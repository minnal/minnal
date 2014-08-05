/**
 * 
 */
package org.minnal.security.filter.cas;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;

import org.jasig.cas.client.ssl.AnyHostnameVerifier;
import org.minnal.security.auth.cas.AbstractPgtTicketStorage;
import org.minnal.security.config.CasConfiguration;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.session.SessionStore;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CasProxyCallbackFilterTest {
	
	private CasProxyCallbackFilter filter;
	
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
		filter = new CasProxyCallbackFilter(configuration);
		request = mock(Request.class);
		response = mock(Response.class);
		chain = mock(FilterChain.class);
	}

	@Test
	public void shouldStorePgt() {
		when(request.getRelativePath()).thenReturn("/test/proxyCallback");
		when(request.getHeader(CasProxyCallbackFilter.PARAM_PROXY_GRANTING_TICKET_IOU)).thenReturn("testiou");
		when(request.getHeader(CasProxyCallbackFilter.PARAM_PROXY_GRANTING_TICKET)).thenReturn("testpgt");
		filter.doFilter(request, response, chain);
		verify(storage).save("testiou", "testpgt");
	}
	
	@Test
	public void shouldNotStorePgtIfPathIsNotProxyCallback() {
		when(request.getRelativePath()).thenReturn("/proxyCallback/another");
		when(request.getHeader(CasProxyCallbackFilter.PARAM_PROXY_GRANTING_TICKET_IOU)).thenReturn("testiou");
		when(request.getHeader(CasProxyCallbackFilter.PARAM_PROXY_GRANTING_TICKET)).thenReturn("testpgt");
		filter.doFilter(request, response, chain);
		verify(storage, never()).save("testiou", "testpgt");
	}
}
