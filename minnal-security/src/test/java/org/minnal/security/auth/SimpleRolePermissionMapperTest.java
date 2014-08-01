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
		Role role = new Role("role1");
		assertEquals(mapper.getPermissions(role).size(), 2);
		assertEquals(mapper.getPermissions(role), Lists.newArrayList(new Permission("permission1"), new Permission("permission2")));
	}
	
	@Test
	public void shouldLoadUserRolesFromCustomPropertiesFile() {
		mapper = new SimpleRolePermissionMapper("role_permissions.properties");
		Role role = new Role("role1");
		assertEquals(mapper.getPermissions(role).size(), 2);
		assertEquals(mapper.getPermissions(role), Lists.newArrayList(new Permission("permission1"), new Permission("permission2")));
	}
	
	@Test
	public void shouldReturnEmptyListForRoleWithoutPermission() {
		Role role = new Role("role3");
		assertEquals(mapper.getPermissions(role).size(), 0);
	}

}
