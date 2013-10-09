/**
 * 
 */
package org.minnal.core.server;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.minnal.core.serializer.Serializer;
import org.minnal.core.server.exception.BadRequestException;
import org.minnal.utils.http.HttpUtil;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.net.MediaType;

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
		when(httpRequest.getHeader(HttpHeaders.Names.CONTENT_LENGTH)).thenReturn("100");
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
		assertEquals(request.getUri().getPath(), httpRequest.getUri());
		request.addHeader("name", "value");
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("name1", "value1");
		headers.put("name2", "value2");
		request.addHeaders(headers);
		request.getHeaders(Lists.newLinkedList(headers.keySet()));
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
	
	@Test
	public void shouldGetContentAsGivenType() {
		ServerRequest request = spy(new ServerRequest(httpRequest, null));
		ChannelBuffer buffer = mock(ChannelBuffer.class);
		doReturn(buffer).when(request).getContent();
		Serializer serializer = mock(Serializer.class);
		doReturn(serializer).when(request).getSerializer(any(MediaType.class));
		request.getContentAs(Object.class);
		verify(serializer).deserialize(buffer, Object.class);
	}
	
	@Test
	public void shouldGetNullContentIfContentLengthIs0() {
		ServerRequest request = spy(new ServerRequest(httpRequest, null));
		ChannelBuffer buffer = mock(ChannelBuffer.class);
		doReturn(buffer).when(request).getContent();
		doReturn(0L).when(request).getContentLength();
		assertNull(request.getContentAs(Object.class));
	}
	
	@Test(expectedExceptions=BadRequestException.class)
	public void shouldThrowBadRequestIfSerializationFails() {
		ServerRequest request = spy(new ServerRequest(httpRequest, null));
		ChannelBuffer buffer = mock(ChannelBuffer.class);
		doReturn(buffer).when(request).getContent();
		Serializer serializer = mock(Serializer.class);
		doReturn(serializer).when(request).getSerializer(any(MediaType.class));
		when(serializer.deserialize(buffer, Object.class)).thenThrow(new IllegalStateException());
		request.getContentAs(Object.class);
	}
	
	@Test
	public void shouldDecodeQueryParamters() {
		String uri = "/app/resource?" + HttpUtil.encode("key=test 1234&value=test/123");
		when(httpRequest.getUri()).thenReturn(uri);
		ServerRequest request = new ServerRequest(httpRequest, null);
		verify(httpRequest).addHeader("key", "test 1234");
		verify(httpRequest).addHeader("value", "test/123");
	}
	
	@Test
	public void shouldGetContentLength() {
		ServerRequest request = new ServerRequest(httpRequest, null);
		assertEquals(request.getContentLength(), 100L);
	}
}
