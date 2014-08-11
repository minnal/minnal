/**
 * 
 */
package org.minnal.security.auth;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SimpleAuthorizer implements Authorizer {
	
	private RolePermissionMapper permissionMapper = new SimpleRolePermissionMapper();
	
	private UserRoleMapper roleMapper = new SimpleUserRoleMapper();

	/**
	 * @param permissionMapper
	 * @param roleMapper
	 */
	public SimpleAuthorizer(RolePermissionMapper permissionMapper,
			UserRoleMapper roleMapper) {
		this.permissionMapper = permissionMapper;
		this.roleMapper = roleMapper;
	}
	
	public SimpleAuthorizer() {
	}

	@Override
	public boolean authorize(User user, List<String> permissions) {
		if (user.getRoles() == null || user.getRoles().isEmpty()) {
			List<String> roles = roleMapper.getRoles(user);
			if (roles != null) {
				user.setRoles(roles);
			}
		}
		if (user.getPermissions() == null || user.getPermissions().isEmpty()) {
			List<String> perms = new ArrayList<String>();
			if (user.getRoles() != null) {
				for (String role : user.getRoles()) {
					List<String> rolePerms = permissionMapper.getPermissions(role);
					if (rolePerms != null) {
						perms.addAll(rolePerms);
					}
				}
			}
			user.setPermissions(perms);
		}
		return user.hasPermissions(permissions);
	}

	/**
	 * @return the permissionMapper
	 */
	public RolePermissionMapper getPermissionMapper() {
		return permissionMapper;
	}

	/**
	 * @param permissionMapper the permissionMapper to set
	 */
	public void setPermissionMapper(RolePermissionMapper permissionMapper) {
		this.permissionMapper = permissionMapper;
	}

	/**
	 * @return the roleMapper
	 */
	public UserRoleMapper getRoleMapper() {
		return roleMapper;
	}

	/**
	 * @param roleMapper the roleMapper to set
	 */
	public void setRoleMapper(UserRoleMapper roleMapper) {
		this.roleMapper = roleMapper;
	}

	@Override
	public boolean authorize(User user, String permission) {
		return authorize(user, Lists.newArrayList(permission));
	}

}
