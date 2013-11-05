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
public class EntityMetaData extends SecurableMetaData {

	private Class<?> entityClass;
	
	private String entityKey = "id";
	
	private Set<ParameterMetaData> searchFields = new HashSet<ParameterMetaData>();
	
	private Set<ActionMetaData> actionMethods = new HashSet<ActionMetaData>();
	
	private Set<CollectionMetaData> collections = new HashSet<CollectionMetaData>();
	
	private Set<AssociationMetaData> associations = new HashSet<AssociationMetaData>();
	
	public EntityMetaData(Class<?> entityClass) {
		super(entityClass.getSimpleName());
		this.entityClass = entityClass;
		super.init(entityClass);
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

	/**
	 * @param entityKey the entityKey to set
	 */
	public void setEntityKey(String entityKey) {
		this.entityKey = entityKey;
	}

	public void addAssociation(AssociationMetaData associationMetaData) {
		associations.add(associationMetaData);
	}

	/**
	 * @return the associations
	 */
	public Set<AssociationMetaData> getAssociations() {
		return associations;
	}

}
