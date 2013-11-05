/**
 * 
 */
package org.minnal.security.auth;

import java.util.ArrayList;
import java.util.List;

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
	public boolean authorize(Principal principal, List<Permission> permissions) {
		User user = (User) principal;
		if (user.getRoles() == null) {
			user.setRoles(roleMapper.getRoles(user));
		}
		if (user.getPermissions() == null) {
			List<Permission> perms = new ArrayList<Permission>();
			for (Role role : user.getRoles()) {
				perms.addAll(permissionMapper.getPermissions(role));
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

}
