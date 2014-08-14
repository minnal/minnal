/**
 * 
 */
package org.minnal.api.filter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.MultivaluedMap;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CorsFilterTest {

	@Test
	public void shouldAddCorsHeaders() {
		CorsFilter filter = new CorsFilter();
		ContainerRequestContext reqContext = mock(ContainerRequestContext.class);
		ContainerResponseContext resContext = mock(ContainerResponseContext.class);
		MultivaluedMap<String, Object> headers = mock(MultivaluedMap.class);
		when(resContext.getHeaders()).thenReturn(headers);
		filter.filter(reqContext, resContext);
		verify(headers).add("Access-Control-Allow-Origin", "*");
		verify(headers).add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
	}

	@Test
	public void shouldCopyAccessControleHeadersToResponse() {
		CorsFilter filter = new CorsFilter();
		ContainerRequestContext reqContext = mock(ContainerRequestContext.class);
		when(reqContext.getHeaderString("Access-Control-Request-Headers")).thenReturn("*");
		ContainerResponseContext resContext = mock(ContainerResponseContext.class);
		MultivaluedMap<String, Object> resHeaders = mock(MultivaluedMap.class);
		when(resContext.getHeaders()).thenReturn(resHeaders);
		filter.filter(reqContext, resContext);
		verify(resHeaders).add("Access-Control-Allow-Headers", "*");
	}
}
