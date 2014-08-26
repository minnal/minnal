/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class PathAnnotationHandlerTest {

	private PathAnnotationHandler handler;
	
	private ResourceMetaData metaData;
	
	@BeforeMethod
	public void setup() {
		handler = new PathAnnotationHandler();
		metaData = new ResourceMetaData(DummyResource.class, "/dummy");
	}
	
	@Test
	public void shouldGetAnnotationType() {
		assertEquals(handler.getAnnotationType(), Path.class);
	}
	
	@Test
	public void shouldAddSubResourceLocatorToResource() throws Exception {
		Path path = mock(Path.class);
		when(path.value()).thenReturn("/sub");
		handler.handle(metaData, path, DummyResource.class.getMethod("subResource"));
		assertTrue(! metaData.getSubResources().isEmpty());
		assertEquals(metaData.getSubResources().iterator().next(), new ResourceMetaData(DummySubResource.class, "/dummy/sub"));
	}
	
	@Test
	public void shouldNotAddSubResourceMethodToResource() throws Exception {
		Path path = mock(Path.class);
		when(path.value()).thenReturn("/sub1");
		handler.handle(metaData, path, DummyResource.class.getMethod("methodWithHttpMethodAnnotation"));
		assertTrue(metaData.getSubResources().isEmpty());
	}
	
	@Path("/dummy")
	public static class DummyResource {
		@Path("/sub")
		public DummySubResource subResource() {return null;}
		@GET
		@Path("/sub1")
		public void methodWithHttpMethodAnnotation() {}
	}
	
	public static class DummySubResource {
		
	}
}
