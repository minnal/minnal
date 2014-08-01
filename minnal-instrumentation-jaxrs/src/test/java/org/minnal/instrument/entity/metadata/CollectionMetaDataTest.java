/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.Set;

import org.minnal.instrument.entity.Collection;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.SecureMultiple;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

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
	
	@Test
	public void shouldPopulatePermissionMetaDataFromField() {
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "dummyModels", DummyModel.class, Set.class, true);
		assertEquals(data.getPermissionMetaData().size(), 1);
		assertEquals(data.getPermissionMetaData().iterator().next(), new PermissionMetaData(Method.POST.getMethod(), Arrays.asList("permission1")));
	}
	
	@Test
	public void shouldPopulatePermissionMetaDataFromMethod() {
		CollectionMetaData data = new CollectionMetaData(DummyModel.class, "anotherDummyModels", DummyModel.class, Set.class, true);
		assertEquals(data.getPermissionMetaData().size(), 2);
		assertEquals(data.getPermissionMetaData(), Sets.newHashSet(new PermissionMetaData(Method.GET.getMethod(), Arrays.asList("permission2", "permission3")), 
				new PermissionMetaData(Method.PUT.getMethod(), Arrays.asList("permission2"))));
	}

	
	public static class DummyModel {
		
		@Secure(method=Method.POST, permissions="permission1")
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
		@SecureMultiple({@Secure(method=Method.GET, permissions={"permission2", "permission3"}), 
			@Secure(method=Method.PUT, permissions={"permission2"})})
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
