/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.testng.Assert.*;
import java.util.Set;

import org.minnal.instrument.entity.Collection;
import org.testng.annotations.Test;

/**
 * @author ganeshs
 *
 */
public class CollectionMetaDataTest {
	
	@Test
	public void shouldPopulateCrudStatusFromField() {
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "dummyModels", DummyModel.class, Set.class, true);
		assertTrue(data.isCreateAllowed());
		assertFalse(data.isUpdateAllowed());
	}
	
	@Test
	public void shouldPopulateCrudStatusFromMethod() {
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "anotherDummyModels", DummyModel.class, Set.class, true);
		assertFalse(data.isCreateAllowed());
		assertTrue(data.isUpdateAllowed());
	}

	
	public static class DummyModel {
		
		@Collection(update=false)
		private Set<DummyModel> dummyModels;
		
		private Set<DummyModel> anotherDummyModels;

		/**
		 * @return the dummyModels
		 */
		public Set<DummyModel> getDummyModels() {
			return dummyModels;
		}

		/**
		 * @param dummyModels the dummyModels to set
		 */
		public void setDummyModels(Set<DummyModel> dummyModels) {
			this.dummyModels = dummyModels;
		}

		/**
		 * @return the anotherDummyModels
		 */
		@Collection(create=false)
		public Set<DummyModel> getAnotherDummyModels() {
			return anotherDummyModels;
		}

		/**
		 * @param anotherDummyModels the anotherDummyModels to set
		 */
		public void setAnotherDummyModels(Set<DummyModel> anotherDummyModels) {
			this.anotherDummyModels = anotherDummyModels;
		}
	}
}
