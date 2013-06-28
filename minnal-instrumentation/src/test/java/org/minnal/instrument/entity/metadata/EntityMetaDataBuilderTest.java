/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.EntityKey;
import org.minnal.instrument.entity.Searchable;
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
		assertEquals(metaData.getCollections().size(), 2);
		assertEquals(metaData.getAssociations().size(), 2);
	}
	
	@Test
	public void shouldBuildUnderEntityMetaData() {
		EntityMetaDataBuilder builder = new EntityMetaDataBuilder(DummyUnderEntity.class);
		EntityMetaData metaData = builder.build();
		assertEquals(metaData.getSearchFields().size(), 2);
	}
	
	class DummyEntity{
		
		@Searchable
		String field1;
		
		@OneToMany
		Set<DummyEntity> field2;
		
		@EntityKey
		String field3;
		
		@ManyToOne
		String field5;
		
		@ManyToMany
		String field6;
		
		@OneToOne
		String field7;
		
		@Action
		public String getField4() {
			return null;
		}
	}
	
	class DummyUnderEntity extends DummyEntity{
		
		@Searchable
		private String field8;

		/**
		 * @return the field8
		 */
		public String getField8() {
			return field8;
		}

		/**
		 * @param field8 the field8 to set
		 */
		public void setField8(String field8) {
			this.field8 = field8;
		}
		 
	}
}
