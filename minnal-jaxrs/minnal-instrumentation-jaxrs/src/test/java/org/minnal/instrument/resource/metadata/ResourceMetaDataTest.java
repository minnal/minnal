/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

import javax.ws.rs.Path;

import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaDataTest {

	@Test
	public void shouldGetResourceMethods() {
		ResourceMetaData metaData = new ResourceMetaData(DummyResource.class, "/dummy");
		metaData.addResourceMethod(mock(ResourceMethodMetaData.class));
		assertEquals(metaData.getResourceMethods().size(), 1);
	}
	
	@Test
	public void shouldGetSubResources() {
		ResourceMetaData metaData = new ResourceMetaData(DummyResource.class, "/dummy");
		metaData.addSubResource(mock(ResourceMetaData.class));
		assertEquals(metaData.getSubResources().size(), 1);
	}
	
	@Test
	public void shouldGetAllResourceMethods() {
		ResourceMetaData metaData = new ResourceMetaData(DummyResource.class, "/dummy");
		metaData.addResourceMethod(mock(ResourceMethodMetaData.class));
		ResourceMetaData subResource = mock(ResourceMetaData.class);
		when(subResource.getAllResourceMethods()).thenReturn(Sets.newHashSet(mock(ResourceMethodMetaData.class), mock(ResourceMethodMetaData.class)));
		metaData.addSubResource(subResource);
		assertEquals(metaData.getAllResourceMethods().size(), 3);
	}
	
	@Path("/dummy")
	class DummyResource {
		
	}
}
