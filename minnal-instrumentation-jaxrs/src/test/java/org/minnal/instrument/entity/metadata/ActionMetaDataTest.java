/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import static org.testng.Assert.assertEquals;

import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ActionMetaDataTest {
	
	@Test
	public void shouldPopulatePermissionMetaData() throws SecurityException, NoSuchMethodException {
		ActionMetaData data = new ActionMetaData("dummyAction", "", DummyModel.class.getMethod("dummyAction"));
		assertEquals(data.getPermissionMetaData().size(), 1);
		assertEquals(data.getPermissionMetaData().iterator().next(), new PermissionMetaData(Method.POST.getMethod(), Sets.newHashSet("permission1")));
	}
	
	@Test
	public void shouldNotPopulatePermissionMetaData() throws SecurityException, NoSuchMethodException {
		ActionMetaData data = new ActionMetaData("anotherAction", "", DummyModel.class.getMethod("anotherAction"));
		assertEquals(data.getPermissionMetaData().size(), 0);
	}

	public static class DummyModel {
		
		@Secure(method=Method.POST, permissions="permission1")
		@Action(value="dummyAction")
		public void dummyAction() {
		}
		
		@Action(value="anotherAction")
		public void anotherAction() {
		}
	}
}
