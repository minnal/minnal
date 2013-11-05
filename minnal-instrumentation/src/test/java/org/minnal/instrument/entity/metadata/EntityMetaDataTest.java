/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.Arrays;

import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.SecureMultiple;
import org.minnal.instrument.entity.Secure.Method;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import static org.testng.Assert.*;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataTest {

	@Test
	public void shouldPopulatePermissionMetaData() {
		EntityMetaData metaData = new EntityMetaData(DummyAggregateRoot.class);
		assertEquals(metaData.getPermissionMetaData().size(), 1);
		assertEquals(metaData.getPermissionMetaData(), Sets.newHashSet(new PermissionMetaData(Method.GET.getMethod(), Arrays.asList("permission1"))));
	}
	
	@Test
	public void shouldNotPopulatePermissionMetaDataForNonAggregateRoot() {
		EntityMetaData metaData = new EntityMetaData(DummyModel.class);
		assertEquals(metaData.getPermissionMetaData().size(), 0);
	}
	
	@Test
	public void shouldPopulateMultiplePermissionMetaData() {
		EntityMetaData metaData = new EntityMetaData(DummyAggregateRootWithMultiplePermissions.class);
		assertEquals(metaData.getPermissionMetaData().size(), 2);
		assertEquals(metaData.getPermissionMetaData(), Sets.newHashSet(new PermissionMetaData(Method.GET.getMethod(), Arrays.asList("permission1")), 
				new PermissionMetaData(Method.POST.getMethod(), Arrays.asList("permission2", "permission3"))));
	}
	
	@Secure(method=Method.GET, permissions="permission1")
	public static class DummyModel {
	}
	
	@AggregateRoot
	@Secure(method=Method.GET, permissions="permission1")
	public static class DummyAggregateRoot {
	}
	
	@AggregateRoot
	@SecureMultiple({
	@Secure(method=Method.GET, permissions="permission1"),
	@Secure(method=Method.POST, permissions={"permission2", "permission3"})
	})
	public static class DummyAggregateRootWithMultiplePermissions {
	}
}
