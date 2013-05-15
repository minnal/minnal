/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import javax.persistence.OneToMany;

import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.EntityKey;
import org.minnal.instrument.entity.Searchable;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaDataBuilder;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataBuilderTest {
	
	@Test
	public void shouldBuildEntityMetaData() {
		EntityMetaDataBuilder builder = new EntityMetaDataBuilder(DummyEntity.class);
		EntityMetaData metaData = builder.build();
		assertEquals(metaData.getActionMethods().size(), 1);
		assertEquals(metaData.getSearchFields().size(), 1);
		assertEquals(metaData.getEntityKey(), "field3");
		assertEquals(metaData.getCollections().size(), 1);
	}
	
	class DummyEntity {
		
		@Searchable
		String field1;
		
		@OneToMany
		Set<DummyEntity> field2;
		
		@EntityKey
		String field3;
		
		@Action
		public String getField4() {
			return null;
		}
	}
}
