/**
 * 
 */
package org.minnal.instrument.entity.metadata;

/**
 * @author ganeshs
 *
 */
public class CollectionMetaData extends MetaData {

	private Class<?> elementType;
	
	private Class<?> type;
	
	private boolean entity;
	
	public CollectionMetaData(String name, Class<?> elementType, Class<?> type, boolean entity) {
		super(name);
		this.elementType = elementType;
		this.type = type;
		this.entity = entity;
	}

	public Class<?> getElementType() {
		return elementType;
	}

	public Class<?> getType() {
		return type;
	}

	/**
	 * @return the entity
	 */
	public boolean isEntity() {
		return entity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((elementType == null) ? 0 : elementType.hashCode());
		result = prime * result + (entity ? 1231 : 1237);
		result = prime * result + ((getName() == null) ? 0 : getName().hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		CollectionMetaData other = (CollectionMetaData) obj;
		if (elementType == null) {
			if (other.elementType != null)
				return false;
		} else if (!elementType.equals(other.elementType))
			return false;
		if (entity != other.entity)
			return false;
		if (getName() == null) {
			if (other.getName() != null)
				return false;
		} else if (!getName().equals(other.getName()))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
