/**
 * 
 */
package org.minnal.security.auth;

import java.util.HashMap;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import static org.testng.Assert.*;

/**
 * @author ganeshs
 *
 */
public class SimpleUserRoleMapperTest {

	private SimpleUserRoleMapper mapper;

	@BeforeMethod
	public void setup() {
		mapper = new SimpleUserRoleMapper();
	}
	
	@Test
	public void shouldLoadUserRolesFromDefaultProperties() {
		User user = new User("user1", new HashMap<String, Object>());
		assertEquals(mapper.getRoles(user).size(), 2);
		assertEquals(mapper.getRoles(user), Lists.newArrayList(new Role("role1"), new Role("role2")));
	}
	
	@Test
	public void shouldLoadUserRolesFromCustomPropertiesFile() {
		mapper = new SimpleUserRoleMapper("user_roles.properties");
		User user = new User("user1", new HashMap<String, Object>());
		assertEquals(mapper.getRoles(user).size(), 2);
		assertEquals(mapper.getRoles(user), Lists.newArrayList(new Role("role1"), new Role("role2")));
	}
	
	@Test
	public void shouldReturnEmptyListForUserWithoutRole() {
		User user = new User("user3", new HashMap<String, Object>());
		assertEquals(mapper.getRoles(user).size(), 0);
	}
}
