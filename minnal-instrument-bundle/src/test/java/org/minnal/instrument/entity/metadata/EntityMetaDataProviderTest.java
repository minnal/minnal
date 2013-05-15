/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.minnal.instrument.entity.DummyModel;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataProviderTest {

	private EntityMetaDataProvider provider;
	
	@BeforeMethod
	public void setup() {
		provider = spy(EntityMetaDataProvider.instance());
	}
	
	@Test
	public void shouldCallBuilderOnlyFirstTime() {
		EntityMetaDataBuilder builder = mock(EntityMetaDataBuilder.class);
		when(builder.build()).thenReturn(mock(EntityMetaData.class));
		doReturn(builder).when(provider).getEntityMetaDataBuilder(DummyModel.class);
		provider.getEntityMetaData(DummyModel.class);
		verify(builder).build();
		provider.getEntityMetaData(DummyModel.class);
		verify(builder, times(1)).build();
	}
	
}
