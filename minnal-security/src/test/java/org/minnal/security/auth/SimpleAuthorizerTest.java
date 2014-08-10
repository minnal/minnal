/**
 * 
 */
package org.minnal.security.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.util.Arrays;

import org.pac4j.core.profile.UserProfile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleAuthorizerTest {
	
	private SimpleAuthorizer authorizer;
	
	private RolePermissionMapper rolePermissionMapper;
	
	private UserRoleMapper userRoleMapper;
	
	private User user;
	
	@BeforeMethod
	public void setup() {
		userRoleMapper = mock(UserRoleMapper.class);
		when(userRoleMapper.getRoles(user)).thenReturn(Arrays.asList("role1", "role2"));
		rolePermissionMapper = mock(RolePermissionMapper.class);
		when(rolePermissionMapper.getPermissions("role1")).thenReturn(Arrays.asList("permission1", "permission2"));
		when(rolePermissionMapper.getPermissions("role2")).thenReturn(Arrays.asList("permission2"));
		authorizer = new SimpleAuthorizer(rolePermissionMapper, userRoleMapper);
		UserProfile profile = new UserProfile();
		profile.setId("name1");
		user = new User(profile);
	}

	@Test
	public void shouldAuthorizeIfAllPermissionsMatch() {
		assertTrue(authorizer.authorize(user, user.getPermissions()));
	}
	
	@Test
	public void shouldAuthorizeIfPermissionMatch() {
		user.setPermissions(Arrays.asList("permission1"));
		assertTrue(authorizer.authorize(user, "permission1"));
	}
	
	@Test
	public void shouldNotAuthorizeIfAllPermissionsDontMatch() {
		assertFalse(authorizer.authorize(user, Arrays.asList("permission1", "permission3")));
	}
	
	@Test
	public void shouldNotAuthorizeIfPermissionDontMatch() {
		assertFalse(authorizer.authorize(user, "permission3"));
	}
	
	@Test
	public void shouldGetRolesFromPropertiesIfNotPopulatedAlready() {
		user.setPermissions(Lists.newArrayList("permission3", "permission1"));
		authorizer.authorize(user, Lists.newArrayList("permission3"));
		verify(userRoleMapper).getRoles(user);
	}
	
	@Test
	public void shouldGetPermissionsFromPropertiesIfNotPopulatedAlready() {
		user.setRoles(Lists.newArrayList("role2"));
		authorizer.authorize(user, Lists.newArrayList("permission3"));
		verify(rolePermissionMapper).getPermissions("role2");
	}
}
