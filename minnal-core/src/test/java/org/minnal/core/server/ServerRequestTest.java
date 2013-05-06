/**
 * 
 */
package org.minnal.core.server;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ServerRequestTest {
	
	private HttpRequest httpRequest;
	
	@BeforeMethod
	public void setup() {
		httpRequest = mock(HttpRequest.class);
		when(httpRequest.getMethod()).thenReturn(HttpMethod.HEAD);
		when(httpRequest.getContent()).thenReturn(mock(ChannelBuffer.class));
		when(httpRequest.getHeader(anyString())).thenReturn("dummy");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("n1", "v1");
		headers.put("n2", "v2");
		when(httpRequest.getHeaders()).thenReturn(new ArrayList<Entry<String, String>>(headers.entrySet()));
		when(httpRequest.getUri()).thenReturn("/app/resource");
	}
	
	@Test
	public void shouldVerifyDelegations() {
		ServerRequest request = new ServerRequest(httpRequest, null);
		assertEquals(request.getHttpMethod(), HttpMethod.HEAD);
		assertEquals(request.getContent(), httpRequest.getContent());
		assertEquals(request.getHeader("test"), "dummy");
		assertEquals(request.getPath(), httpRequest.getUri());
		request.getHeaders();
		request.addHeader("name", "value");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("name1", "value1");
		headers.put("name2", "value2");
		request.addHeaders(headers);
		verify(httpRequest).getHeaders();
		verify(httpRequest).addHeader("name", "value");
		verify(httpRequest).addHeader("name1", "value1");
		verify(httpRequest).addHeader("name2", "value2");
	}
	
	@Test
	public void shouldComputeRelativePathOfRequest() {
		ServerRequest request = new ServerRequest(httpRequest, null);
		request.setApplicationPath("/app");
		assertEquals(request.getRelativePath(), "/resource");
	}
}
