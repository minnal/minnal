/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.Set;

/**
 * @author ganeshs
 *
 */
public class PermissionMetaData {
	
	private String method;
	
	private Set<String> permissions;

	/**
	 * @param method
	 * @param permissions
	 */
	public PermissionMetaData(String method, Set<String> permissions) {
		this.method = method;
		this.permissions = permissions;
	}

	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @return the permissions
	 */
	public Set<String> getPermissions() {
		return permissions;
	}

	/**
	 * @param permissions the permissions to set
	 */
	public void setPermissions(Set<String> permissions) {
		this.permissions = permissions;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result
				+ ((permissions == null) ? 0 : permissions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PermissionMetaData other = (PermissionMetaData) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (permissions == null) {
			if (other.permissions != null)
				return false;
		} else if (!permissions.equals(other.permissions))
			return false;
		return true;
	}

}
