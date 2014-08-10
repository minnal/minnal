/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMethodMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class HttpMethodAnnotationHandlerTest {
	
	private ResourceMetaData metaData;
	
	@BeforeMethod
	public void setup() {
		metaData = new ResourceMetaData(DummyResource.class, "/dummy");
	}

	@Test
	public void shouldHandleGETAnnotation() throws Exception {
		GETAnnotationHandler handler = new GETAnnotationHandler();
		handler.handle(metaData, mock(GET.class), DummyResource.class.getMethod("get"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.GET, DummyResource.class.getMethod("get")));
	}
	
	@Test
	public void shouldHandleGETAnnotationWithPath() throws Exception {
		GETAnnotationHandler handler = new GETAnnotationHandler();
		handler.handle(metaData, mock(GET.class), DummyResource.class.getMethod("getWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/get", HttpMethod.GET, DummyResource.class.getMethod("getWithPath")));
	}
	
	@Test
	public void shouldHandlePOSTAnnotation() throws Exception {
		POSTAnnotationHandler handler = new POSTAnnotationHandler();
		handler.handle(metaData, mock(POST.class), DummyResource.class.getMethod("post"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.POST, DummyResource.class.getMethod("post")));
	}
	
	@Test
	public void shouldHandlePOSTAnnotationWithPath() throws Exception {
		POSTAnnotationHandler handler = new POSTAnnotationHandler();
		handler.handle(metaData, mock(POST.class), DummyResource.class.getMethod("postWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/post", HttpMethod.POST, DummyResource.class.getMethod("postWithPath")));
	}
	
	@Test
	public void shouldHandlePUTAnnotation() throws Exception {
		PUTAnnotationHandler handler = new PUTAnnotationHandler();
		handler.handle(metaData, mock(PUT.class), DummyResource.class.getMethod("put"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.PUT, DummyResource.class.getMethod("put")));
	}
	
	@Test
	public void shouldHandlePUTAnnotationWithPath() throws Exception {
		PUTAnnotationHandler handler = new PUTAnnotationHandler();
		handler.handle(metaData, mock(PUT.class), DummyResource.class.getMethod("putWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/put", HttpMethod.PUT, DummyResource.class.getMethod("putWithPath")));
	}
	
	@Test
	public void shouldHandleDELETEAnnotation() throws Exception {
		DELETEAnnotationHandler handler = new DELETEAnnotationHandler();
		handler.handle(metaData, mock(DELETE.class), DummyResource.class.getMethod("delete"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.DELETE, DummyResource.class.getMethod("delete")));
	}
	
	@Test
	public void shouldHandleDELETEAnnotationWithPath() throws Exception {
		DELETEAnnotationHandler handler = new DELETEAnnotationHandler();
		handler.handle(metaData, mock(DELETE.class), DummyResource.class.getMethod("deleteWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/delete", HttpMethod.DELETE, DummyResource.class.getMethod("deleteWithPath")));
	}
	
	@Test
	public void shouldHandleHEADAnnotation() throws Exception {
		HEADAnnotationHandler handler = new HEADAnnotationHandler();
		handler.handle(metaData, mock(HEAD.class), DummyResource.class.getMethod("head"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.HEAD, DummyResource.class.getMethod("head")));
	}
	
	@Test
	public void shouldHandleHEADAnnotationWithPath() throws Exception {
		HEADAnnotationHandler handler = new HEADAnnotationHandler();
		handler.handle(metaData, mock(HEAD.class), DummyResource.class.getMethod("headWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/head", HttpMethod.HEAD, DummyResource.class.getMethod("headWithPath")));
	}
	
	@Test
	public void shouldHandleOPTIONSAnnotation() throws Exception {
		OPTIONSAnnotationHandler handler = new OPTIONSAnnotationHandler();
		handler.handle(metaData, mock(OPTIONS.class), DummyResource.class.getMethod("options"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy", HttpMethod.OPTIONS, DummyResource.class.getMethod("options")));
	}
	
	@Test
	public void shouldHandleOPTIONSAnnotationWithPath() throws Exception {
		OPTIONSAnnotationHandler handler = new OPTIONSAnnotationHandler();
		handler.handle(metaData, mock(OPTIONS.class), DummyResource.class.getMethod("optionsWithPath"));
		assertTrue(! metaData.getResourceMethods().isEmpty());
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/options", HttpMethod.OPTIONS, DummyResource.class.getMethod("optionsWithPath")));
	}
	
	@Path("/dummy")
	public static class DummyResource {
		@GET
		public void get() {}
		@GET
		@Path("/get")
		public void getWithPath() {}
		@PUT
		public void put() {}
		@PUT
		@Path("/put")
		public void putWithPath() {}
		@POST
		public void post() {}
		@POST
		@Path("/post")
		public void postWithPath() {}
		@DELETE
		public void delete() {}
		@DELETE
		@Path("/delete")
		public void deleteWithPath() {}
		@HEAD
		public void head() {}
		@HEAD
		@Path("/head")
		public void headWithPath() {}
		@OPTIONS
		public void options() {}
		@OPTIONS
		@Path("/options")
		public void optionsWithPath() {}
	}
}
