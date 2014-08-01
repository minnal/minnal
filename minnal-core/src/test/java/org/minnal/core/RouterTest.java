/**
 * 
 */
package org.minnal.core;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

import java.io.OutputStream;
import java.net.URI;
import java.util.Arrays;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.server.ContainerRequest;
import org.glassfish.jersey.server.ContainerResponse;
import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.server.MessageContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class RouterTest {
	
	private Router router;
	
	private MessageContext context;
	
	private Application<ApplicationConfiguration> application;
	
	private ApplicationMapping applicationMapping;
	
	private FullHttpRequest request;
	
	private FullHttpResponse response;
	
	@BeforeMethod
	public void setup() {
		applicationMapping = mock(ApplicationMapping.class);
		request = mock(FullHttpRequest.class);
		response = mock(FullHttpResponse.class);
		when(request.getMethod()).thenReturn(HttpMethod.GET);
		when(request.getUri()).thenReturn("/test");
		when(request.headers()).thenReturn(new DefaultHttpHeaders());
		when(request.content()).thenReturn(mock(ByteBuf.class));
		when(request.getProtocolVersion()).thenReturn(HttpVersion.HTTP_1_1);
		when(response.getStatus()).thenReturn(HttpResponseStatus.PROCESSING);
		application = mock(Application.class);
		context = mock(MessageContext.class);
		router = spy(new Router(applicationMapping));
		doReturn(new ApplicationHandler()).when(router).getApplicationHandler(application);
		when(application.getPath()).thenReturn(URI.create("/app"));
		when(context.getRequest()).thenReturn(request);
		when(context.getResponse()).thenReturn(response);
		when(context.getApplication()).thenReturn(application);
		when(context.getBaseUri()).thenReturn(URI.create("http://localhost:8080"));
		when(applicationMapping.resolve(request)).thenReturn(application);
	}

	@Test
	public void shouldNotSetResponseCodeIfAlreadySet() {
		when(response.getStatus()).thenReturn(HttpResponseStatus.NOT_FOUND);
		router.route(context);
		verify(response, never()).setStatus(any(HttpResponseStatus.class));
	}
	
	@Test
	public void shouldPopulateContextWithApplication() {
		router.route(context);
		verify(context).setApplication(application);
	}
	
	@Test(expectedExceptions=NotFoundException.class)
	public void shouldReturnNotFoundIfApplicationDoesntMatch() {
		RouterListener listener = mock(RouterListener.class);
		router.registerListener(listener);
		when(applicationMapping.resolve(context.getRequest())).thenReturn(null);
		router.route(context);
		verify(listener, never()).onApplicationResolved(context);
	}
	
	@Test
	public void shouldInvokeListenerWhenApplicationIsResolved() {
		RouterListener listener = mock(RouterListener.class);
		router.registerListener(listener);
		router.route(context);
		verify(listener).onApplicationResolved(context);
	}
	
	@Test
	public void shouldInvokeApplicationHandlerOnRoute() {
		ApplicationHandler handler = new ApplicationHandler();
		doReturn(handler).when(router).getApplicationHandler(application);
		router.route(context);
		// FIXME: ApplicationHandler cant be mocked. How to test this ???
	}
	
	@Test
	public void shouldCreateContainerRequestFromHttpRequestWithBaseAndRequestUri() {
		ContainerRequest containerRequest = router.createContainerRequest(context);
		assertEquals(containerRequest.getBaseUri(), URI.create("http://localhost:8080/app"));
		assertEquals(containerRequest.getRequestUri(), URI.create("/test"));
	}
	
	@Test
	public void shouldCreateContainerRequestFromHttpRequestWithHeaders() {
		request.headers().add("header1", "value1");
		request.headers().add("header2", Arrays.asList("value2", "value3"));
		ContainerRequest containerRequest = router.createContainerRequest(context);
		assertEquals(containerRequest.getHeaders().getFirst("header1"), "value1");
		assertEquals(containerRequest.getHeaders().get("header2"), Arrays.asList("value2", "value3"));
	}
	
	@Test
	public void shouldCreateContainerRequestFromHttpRequestWithContent() {
		ByteBuf content = mock(ByteBuf.class);
		when(request.content()).thenReturn(content);
		ContainerRequest containerRequest = router.createContainerRequest(context);
		assertTrue(containerRequest.getEntityStream() instanceof ByteBufInputStream);
	}
	
	@Test
	public void shouldCreateApplicationHandlerFirstTime() {
		router = spy(new Router(applicationMapping));
		ResourceConfig config = mock(ResourceConfig.class);
		when(application.getResourceConfig()).thenReturn(config);
		ApplicationHandler handler = new ApplicationHandler();
		doReturn(handler).when(router).createApplicationHandler(config);
		assertNotNull(router.getApplicationHandler(application));
	}
	
	@Test
	public void shouldNotCreateApplicationHandlerSecondTime() {
		router = spy(new Router(applicationMapping));
		ResourceConfig config = mock(ResourceConfig.class);
		when(application.getResourceConfig()).thenReturn(config);
		ApplicationHandler handler = new ApplicationHandler();
		doReturn(handler).when(router).createApplicationHandler(config);
		handler = router.getApplicationHandler(application);
		assertEquals(router.getApplicationHandler(application), handler);
	}
	
	@Test
	public void shouldCreateHttpResponseFromContainerResponseWithStatus() {
		MultivaluedMap<String, Object> headers = mock(MultivaluedMap.class);
		ContainerResponse response = mock(ContainerResponse.class);
		when(response.getHeaders()).thenReturn(headers);
		when(response.getStatus()).thenReturn(200);
		ByteBuf buffer = mock(ByteBuf.class);
		FullHttpResponse httpResponse = router.createHttpResponse(context, response, buffer);
		assertEquals(httpResponse.getStatus(), HttpResponseStatus.OK);
	}
	
	@Test
	public void shouldCreateHttpResponseFromContainerResponseWithContent() {
		MultivaluedMap<String, Object> headers = mock(MultivaluedMap.class);
		ContainerResponse response = mock(ContainerResponse.class);
		when(response.getHeaders()).thenReturn(headers);
		when(response.getStatus()).thenReturn(200);
		when(response.getLength()).thenReturn(10);
		when(response.getEntityStream()).thenReturn(mock(OutputStream.class));
		ByteBuf buffer = mock(ByteBuf.class);
		FullHttpResponse httpResponse = router.createHttpResponse(context, response, buffer);
		assertTrue(httpResponse.content() instanceof ByteBuf);
		assertEquals(HttpHeaders.getContentLength(httpResponse), 10);
	}
	
	@Test
	public void shouldCreateHttpResponseFromContainerResponseWithHeaders() {
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<String, Object>();
		headers.add("header1", "value1");
		headers.addAll("header2", Arrays.<Object>asList("value2", "value3"));
		ContainerResponse response = mock(ContainerResponse.class);
		when(response.getHeaders()).thenReturn(headers);
		when(response.getStatus()).thenReturn(200);
		ByteBuf buffer = mock(ByteBuf.class);
		FullHttpResponse httpResponse = router.createHttpResponse(context, response, buffer);
		assertEquals(HttpHeaders.getHeader(httpResponse, "header1"), "value1");
		assertEquals(HttpHeaders.getHeader(httpResponse, "header2"), "value2, value3");
	}
}