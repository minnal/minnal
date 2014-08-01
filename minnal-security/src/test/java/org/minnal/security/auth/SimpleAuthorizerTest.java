/**
 * 
 */
package org.minnal.security.auth;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleAuthorizerTest {
	
	private SimpleAuthorizer authorizer;
	
	@BeforeMethod
	public void setup() {
		authorizer = new SimpleAuthorizer();
	}

	@Test
	public void shouldAuthorizeIfPermissionsMatch() {
		User user = new User();
		List<Permission> permissions = Arrays.asList(new Permission("permission1"), new Permission("permission2"));
		user.setPermissions(permissions);
		assertTrue(authorizer.authorize(user, permissions));
	}
	
	@Test
	public void shouldNotAuthorizeIfRolesDontMatch() {
		User user = new User();
		List<Permission> permissions = Arrays.asList(new Permission("permission1"), new Permission("permission2"));
		user.setPermissions(permissions);
		assertFalse(authorizer.authorize(user, Arrays.asList(new Permission("permission1"), new Permission("permission3"))));
	}
	
	@Test
	public void shouldGetRolesFromPropertiesIfNotPopulatedAlready() {
		User user = new User();
		user.setName("user1");
		user.setPermissions(Lists.newArrayList(new Permission("permission3"), new Permission("permission1")));
		assertTrue(authorizer.authorize(user, Lists.newArrayList(new Permission("permission3"))));
	}
	
	@Test
	public void shouldGetPermissionsFromPropertiesIfNotPopulatedAlready() {
		User user = new User();
		user.setName("user1");
		user.setRoles(Lists.newArrayList(new Role("role2")));
		assertTrue(authorizer.authorize(user, Lists.newArrayList(new Permission("permission3"))));
	}
}
