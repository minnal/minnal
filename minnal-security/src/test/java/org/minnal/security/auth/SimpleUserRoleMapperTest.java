/**
 * 
 */
package org.minnal.security.auth;

import static org.testng.Assert.assertEquals;

import org.pac4j.core.profile.UserProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleUserRoleMapperTest {

	private SimpleUserRoleMapper mapper;
	
	private User user;

	@BeforeMethod
	public void setup() {
		mapper = new SimpleUserRoleMapper();
		UserProfile profile = new UserProfile();
		profile.setId("user1");
		user = new User(profile);
	}
	
	@Test
	public void shouldLoadUserRolesFromDefaultProperties() {
		assertEquals(mapper.getRoles(user).size(), 2);
		assertEquals(mapper.getRoles(user), Lists.newArrayList("role1", "role2"));
	}
	
	@Test
	public void shouldLoadUserRolesFromCustomPropertiesFile() {
		mapper = new SimpleUserRoleMapper("user_roles.properties");
		assertEquals(mapper.getRoles(user).size(), 2);
		assertEquals(mapper.getRoles(user), Lists.newArrayList("role1", "role2"));
	}
	
	@Test
	public void shouldReturnEmptyListForUserWithoutRole() {
		UserProfile profile = new UserProfile();
		profile.setId("user3");
		User user = new User(profile);
		assertEquals(mapper.getRoles(user).size(), 0);
	}
}
