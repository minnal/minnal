/**
 * 
 */
package org.minnal.instrument.resource;

import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ResourcePath {
	
	private EntityNodePath nodePath;
	
	private boolean bulk;
	
	public ResourcePath(EntityNodePath nodePath, boolean bulk) {
		this.nodePath = nodePath;
		this.bulk = bulk;
	}
	
	/**
	 * @return the nodePath
	 */
	public EntityNodePath getNodePath() {
		return nodePath;
	}

	/**
	 * @return the bulk
	 */
	public boolean isBulk() {
		return bulk;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (bulk ? 1231 : 1237);
		result = prime * result
				+ ((nodePath == null) ? 0 : nodePath.hashCode());
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
		ResourcePath other = (ResourcePath) obj;
		if (bulk != other.bulk)
			return false;
		if (nodePath == null) {
			if (other.nodePath != null)
				return false;
		} else if (!nodePath.equals(other.nodePath))
			return false;
		return true;
	}
}
