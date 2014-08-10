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

import org.activejpa.jpa.JPAContext;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.minnal.core.config.DatabaseConfiguration;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class OpenSessionInViewFilterTest {
	
	private OpenSessionInViewFilter filter;
	
	private ContainerRequest request;
	
	private ContainerResponse response;
	
	private JPAContext context;
	
	private DatabaseConfiguration configuration;

	@BeforeMethod
	public void setup() {
		configuration = mock(DatabaseConfiguration.class);
		filter = spy(new OpenSessionInViewFilter(configuration));
		context = mock(JPAContext.class);
		request = mock(ContainerRequest.class);
		response = mock(ContainerResponse.class);
		doReturn(context).when(filter).getContext();
	}
	
	@Test
	public void shouldInitializeEntityManagerWhenRequestReceived() throws IOException {
		filter.requestReceived(request);
		verify(context).getEntityManager();
	}
	
	@Test
	public void shouldRollbackTransactionIfTrasactionIsOpenWhenRequestCompleted() throws IOException {
		filter.requestReceived(request);
		when(context.isTxnOpen()).thenReturn(true);
		filter.requestCompleted(request, response);
		verify(context).closeTxn(true);
	}
	
	@Test
	public void shouldCloseContextInResponseFilter() throws IOException {
		filter.requestReceived(request);
		filter.requestCompleted(request, response);
		verify(context).close();
	}
}
