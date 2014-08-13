/**
 * 
 */
package org.minnal.security.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.message.internal.OutboundMessageContext;
import org.minnal.security.session.Session;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class JaxrsWebContextTest {

	private JaxrsWebContext context;
	
	private ContainerRequestContext request;
	
	private OutboundMessageContext outboundMessageContext;
	
	private Session session;
	
	@BeforeMethod
	public void setup() {
		session = mock(Session.class);
		request = mock(ContainerRequestContext.class);
		outboundMessageContext = new OutboundMessageContext();
		context = new JaxrsWebContext(request, outboundMessageContext, session);
	}
	
	@Test
	public void shouldGetRequestParameters() {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.addAll("key1", Lists.newArrayList("value1", "value2"));
		params.add("key2", "value3");
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(params);
		when(request.getUriInfo()).thenReturn(uriInfo);
		Map<String, String[]> reqParams = context.getRequestParameters();
		assertEquals(reqParams.get("key1"), new String[]{"value1", "value2"});
		assertEquals(reqParams.get("key2"), new String[]{"value3"});
	}
	
	@Test
	public void shouldGetRequestParameter() {
		MultivaluedMap<String, String> params = new MultivaluedHashMap<String, String>();
		params.addAll("key1", Lists.newArrayList("value1", "value2"));
		params.add("key2", "value3");
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getQueryParameters()).thenReturn(params);
		when(request.getUriInfo()).thenReturn(uriInfo);
		assertEquals(context.getRequestParameter("key1"), "value1");
	}
	
	@Test
	public void shouldGetRequestHeader() {
		when(request.getHeaderString("header1")).thenReturn("value1");
		assertEquals(context.getRequestHeader("header1"), "value1");
	}
	
	@Test
	public void shouldSetSessionAttribute() {
		context.setSessionAttribute("key1", "value1");
		verify(session).addAttribute("key1", "value1");
	}
	
	@Test
	public void shouldGetSessionAttribute() {
		when(session.getAttribute("key1")).thenReturn("value1");
		assertEquals(context.getSessionAttribute("key1"), "value1");
	}
	
	@Test
	public void shouldGetRequestMethod() {
		when(request.getMethod()).thenReturn("GET");
		assertEquals(context.getRequestMethod(), "GET");
	}
	
	@Test
	public void shouldWriteResponseContent() throws IOException {
		OutputStream os = mock(OutputStream.class);
		outboundMessageContext.setEntityStream(os);
		context.writeResponseContent("test");
		verify(os).write("test".getBytes());
	}
	
	@Test
	public void shouldSetResponseStatus() throws IOException {
		context.setResponseStatus(200);
		assertEquals(context.getResponse().getStatusInfo(), Response.Status.OK);
	}
	
	@Test
	public void shouldSetResponseStatusForUnkownCode() throws IOException {
		context.setResponseStatus(499);
		StatusType type = context.getResponse().getStatusInfo();
		assertEquals(type.getFamily(), Family.CLIENT_ERROR);
		assertEquals(type.getStatusCode(), 499);
	}
	
	@Test
	public void shouldSetResponseHeader() {
		context.setResponseHeader("header1", "value1");
		assertEquals(context.getResponse().getHeaderString("header1"), "value1");
	}
	
	@Test
	public void shouldGetServerName() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getRequestUri()).thenReturn(URI.create("http://localhost:8080/test"));
		when(request.getUriInfo()).thenReturn(uriInfo);
		assertEquals(context.getServerName(), "localhost");
	}
	
	@Test
	public void shouldGetScheme() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getRequestUri()).thenReturn(URI.create("https://localhost:8080/test"));
		when(request.getUriInfo()).thenReturn(uriInfo);
		assertEquals(context.getScheme(), "https");
	}
	
	@Test
	public void shouldGetPort() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getRequestUri()).thenReturn(URI.create("https://localhost:8080/test"));
		when(request.getUriInfo()).thenReturn(uriInfo);
		assertEquals(context.getServerPort(), 8080);
	}
	
	@Test
	public void shouldGetFullRequestUrl() {
		UriInfo uriInfo = mock(UriInfo.class);
		when(uriInfo.getRequestUri()).thenReturn(URI.create("https://localhost:8080/test"));
		when(request.getUriInfo()).thenReturn(uriInfo);
		assertEquals(context.getFullRequestURL(), "https://localhost:8080/test");
	}
}
