/**
 * 
 */
package org.minnal.security.auth;

import java.util.List;

/**
 * @author ganeshs
 *
 */
public interface UserRoleMapper {

	List<String> getRoles(User user);
}
