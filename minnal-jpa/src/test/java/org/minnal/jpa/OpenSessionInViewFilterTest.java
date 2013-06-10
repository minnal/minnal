/**
 * 
 */
package org.minnal.jpa;

import static org.mockito.Mockito.*;

import org.activejpa.jpa.JPAContext;
import org.minnal.core.FilterChain;
import org.minnal.core.Request;
import org.minnal.core.Response;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilterTest {
	
	private OpenSessionInViewFilter filter;
	
	private Request request;
	
	private Response response;
	
	private FilterChain chain;
	
	private JPAContext context;
	
	private DatabaseConfiguration configuration;

	@BeforeMethod
	public void setup() {
		configuration = mock(DatabaseConfiguration.class);
		filter = spy(new OpenSessionInViewFilter(configuration));
		context = mock(JPAContext.class);
		request = mock(Request.class);
		response = mock(Response.class);
		chain = mock(FilterChain.class);
		doReturn(context).when(filter).getContext();
	}
	
	@Test
	public void shouldInitializeEntityManagerBeforeForwading() {
		filter.doFilter(request, response, chain);
		inOrder(context, chain);
		verify(context).getEntityManager();
		verify(chain).doFilter(request, response);
	}
	
	@Test
	public void shouldRollbackTransactionIfTrasactionIsOpen() {
		when(context.isTxnOpen()).thenReturn(true);
		filter.doFilter(request, response, chain);
		inOrder(chain, context);
		verify(chain).doFilter(request, response);
		verify(context).closeTxn(true);
	}
	
	@Test
	public void shouldCloseContextAfterForwardingFilter() {
		filter.doFilter(request, response, chain);
		inOrder(chain, context);
		verify(chain).doFilter(request, response);
		verify(context).close();
	}
}
