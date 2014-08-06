/**
 * 
 */
package org.minnal.jpa;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;

import org.activejpa.jpa.JPAContext;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilterTest {
	
	private OpenSessionInViewFilter filter;
	
	private ContainerRequestContext request;
	
	private ContainerResponseContext response;
	
	private JPAContext context;
	
	private DatabaseConfiguration configuration;

	@BeforeMethod
	public void setup() {
		configuration = mock(DatabaseConfiguration.class);
		filter = spy(new OpenSessionInViewFilter(configuration));
		context = mock(JPAContext.class);
		request = mock(ContainerRequestContext.class);
		response = mock(ContainerResponseContext.class);
		doReturn(context).when(filter).getContext();
	}
	
	@Test
	public void shouldInitializeEntityManagerOnRequestFilter() throws IOException {
		filter.filter(request);
		verify(context).getEntityManager();
	}
	
	@Test
	public void shouldRollbackTransactionIfTrasactionIsOpenInResponseFilter() throws IOException {
		filter.filter(request);
		when(context.isTxnOpen()).thenReturn(true);
		filter.filter(request, response);
		verify(context).closeTxn(true);
	}
	
	@Test
	public void shouldCloseContextInResponseFilter() throws IOException {
		filter.filter(request);
		filter.filter(request, response);
		verify(context).close();
	}
}
