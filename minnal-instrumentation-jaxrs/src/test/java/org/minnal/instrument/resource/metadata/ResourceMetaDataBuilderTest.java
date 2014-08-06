/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import static org.testng.Assert.assertEquals;

import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaDataBuilderTest {
	
	@Test
	public void shouldBuildResourceMetaData() throws Exception {
		ResourceMetaDataBuilder builder = new ResourceMetaDataBuilder(DummyResource.class);
		ResourceMetaData metaData = builder.build();
		assertEquals(metaData.getResourceMethods().size(), 1);
		assertEquals(metaData.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/sub1", HttpMethod.GET, DummyResource.class.getMethod("getMethod")));
	}
	
	@Test
	public void shouldBuildSubResourceMetaData() throws Exception {
		ResourceMetaDataBuilder builder = new ResourceMetaDataBuilder(DummyResource.class);
		ResourceMetaData metaData = builder.build();
		assertEquals(metaData.getSubResources().size(), 1);
		ResourceMetaData subResource = metaData.getSubResources().iterator().next();
		assertEquals(subResource.getResourceMethods().size(), 1);
		assertEquals(subResource.getResourceMethods().iterator().next(), new ResourceMethodMetaData("/dummy/sub/get", HttpMethod.GET, DummySubResource.class.getMethod("subGetMethod")));
	}
	
	@Path("/dummy")
	public static class DummyResource {
		@Path("/sub")
		public DummySubResource subResource() {return null;}
		@GET
		@Path("/sub1")
		public void getMethod() {}
	}
	
	public static class DummySubResource {
		@GET
		@Path("/get")
		public void subGetMethod() {}
	}
}
