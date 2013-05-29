/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author ganeshs
 *
 */
public class EntityMetaData extends MetaData {

	private Class<?> entityClass;
	
	private String entityKey = "id";
	
	private Set<ParameterMetaData> searchFields = new HashSet<ParameterMetaData>();
	
	private Set<ActionMetaData> actionMethods = new HashSet<ActionMetaData>();
	
	private Set<CollectionMetaData> collections = new HashSet<CollectionMetaData>();
	
	private Set<AssociationMetaData> associations = new HashSet<AssociationMetaData>();
	
	public EntityMetaData(Class<?> entityClass) {
		super(entityClass.getSimpleName());
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
		TreeSet<ParameterMetaData> treeSet = new TreeSet<ParameterMetaData>(new MetaDataComparator());
		treeSet.addAll(searchFields);
		return treeSet;
	}

	public Set<ActionMetaData> getActionMethods() {
		TreeSet<ActionMetaData> treeSet = new TreeSet<ActionMetaData>(new MetaDataComparator());
		treeSet.addAll(actionMethods);
		return treeSet;
	}

	public Set<CollectionMetaData> getCollections() {
		TreeSet<CollectionMetaData> treeSet = new TreeSet<CollectionMetaData>(new MetaDataComparator());
		treeSet.addAll(collections);
		return treeSet;
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
		TreeSet<AssociationMetaData> treeSet = new TreeSet<AssociationMetaData>(new MetaDataComparator());
		treeSet.addAll(associations);
		return treeSet;
	}
}
