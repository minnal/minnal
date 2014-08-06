/**
 * 
 */
package org.minnal.security.auth;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleRolePermissionMapperTest {

	private SimpleRolePermissionMapper mapper;

	@BeforeMethod
	public void setup() {
		mapper = new SimpleRolePermissionMapper();
	}
	
	@Test
	public void shouldLoadRolePermissionsFromDefaultProperties() {
		String role = "role1";
		assertEquals(mapper.getPermissions(role).size(), 2);
		assertEquals(mapper.getPermissions(role), Lists.newArrayList("permission1", "permission2"));
	}
	
	@Test
	public void shouldLoadUserRolesFromCustomPropertiesFile() {
		mapper = new SimpleRolePermissionMapper("role_permissions.properties");
		String role = "role1";
		assertEquals(mapper.getPermissions(role).size(), 2);
		assertEquals(mapper.getPermissions(role), Lists.newArrayList("permission1", "permission2"));
	}
	
	@Test
	public void shouldReturnEmptyListForRoleWithoutPermission() {
		String role = "role3";
		assertEquals(mapper.getPermissions(role).size(), 0);
	}

}
