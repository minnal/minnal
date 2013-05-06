/**
 * 
 */
package org.minnal.instrument.entity.metadata;

/**
 * @author ganeshs
 *
 */
public class CollectionMetaData {

	private String name;
	
	private Class<?> elementType;
	
	private Class<?> type;
	
	private boolean entity;
	
	public CollectionMetaData(String name, Class<?> elementType, Class<?> type, boolean entity) {
		this.name = name;
		this.elementType = elementType;
		this.type = type;
		this.entity = entity;
	}

	public String getName() {
		return name;
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
}
