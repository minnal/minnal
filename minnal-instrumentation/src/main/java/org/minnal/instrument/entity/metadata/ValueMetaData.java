/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ganeshs
 *
 */
public class ValueMetaData extends MetaData {
	
	private Class<?> type;
	
	private Set<ParameterMetaData> searchFields = new HashSet<ParameterMetaData>();
	
	private Set<CollectionMetaData> collections = new HashSet<CollectionMetaData>();
	
	private Set<AssociationMetaData> associations = new HashSet<AssociationMetaData>();

	/**
	 * @param name
	 */
	public ValueMetaData(String name, Class<?> type) {
		super(name);
		this.type = type;
	}

	/**
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Class<?> type) {
		this.type = type;
	}

	/**
	 * @return the searchFields
	 */
	public Set<ParameterMetaData> getSearchFields() {
		return searchFields;
	}

	/**
	 * @param searchFields the searchFields to set
	 */
	public void setSearchFields(Set<ParameterMetaData> searchFields) {
		this.searchFields = searchFields;
	}

	/**
	 * @return the collections
	 */
	public Set<CollectionMetaData> getCollections() {
		return collections;
	}

	/**
	 * @param collections the collections to set
	 */
	public void setCollections(Set<CollectionMetaData> collections) {
		this.collections = collections;
	}

	/**
	 * @return the associations
	 */
	public Set<AssociationMetaData> getAssociations() {
		return associations;
	}

	/**
	 * @param associations the associations to set
	 */
	public void setAssociations(Set<AssociationMetaData> associations) {
		this.associations = associations;
	}

}
