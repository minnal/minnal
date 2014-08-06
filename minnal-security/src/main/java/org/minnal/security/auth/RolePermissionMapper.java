/**
 * 
 */
package org.minnal.security.auth;

import java.util.List;

/**
 * @author ganeshs
 *
 */
public interface RolePermissionMapper {

	List<String> getPermissions(String role);
}
