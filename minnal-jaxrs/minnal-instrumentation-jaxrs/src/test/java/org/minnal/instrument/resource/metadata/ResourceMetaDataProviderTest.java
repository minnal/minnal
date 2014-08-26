/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaDataProviderTest {

	private ResourceMetaDataProvider provider;
	
	@BeforeMethod
	public void setup() {
		provider = spy(ResourceMetaDataProvider.instance());
	}
	
	@Test
	public void shouldCallBuilderOnlyFirstTime() {
		ResourceMetaDataBuilder builder = mock(ResourceMetaDataBuilder.class);
		when(builder.build()).thenReturn(mock(ResourceMetaData.class));
		doReturn(builder).when(provider).getResourceMetaDataBuilder(DummyResource.class);
		provider.getResourceMetaData(DummyResource.class);
		verify(builder).build();
		provider.getResourceMetaData(DummyResource.class);
		verify(builder, times(1)).build();
	}
	
	@Path("/dummy")
	public static class DummyResource {
		@GET
		public void getMethod() {}
	}
}
