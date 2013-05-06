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
public class EntityMetaData {

	private Class<?> entityClass;
	
	private String entityKey;
	
	private Set<ParameterMetaData> searchFields = new HashSet<ParameterMetaData>();
	
	private Set<ActionMetaData> actionMethods = new HashSet<ActionMetaData>();
	
	private Set<CollectionMetaData> collections = new HashSet<CollectionMetaData>();
	
	public EntityMetaData(Class<?> entityClass) {
		this.entityClass = entityClass;
	}
	
	public void addSearchField(ParameterMetaData parameterMetaData) {
		searchFields.add(parameterMetaData);
	}
	
	public void addActionMethod(ActionMetaData actionMetaData) {
		actionMethods.add(actionMetaData);
	}
	
	public void addCollection(CollectionMetaData collectionMetaData) {
		collections.add(collectionMetaData);
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getEntityKey() {
		return entityKey;
	}

	public Set<ParameterMetaData> getSearchFields() {
		return searchFields;
	}

	public Set<ActionMetaData> getActionMethods() {
		return actionMethods;
	}

	public Set<CollectionMetaData> getCollections() {
		return collections;
	}
}
