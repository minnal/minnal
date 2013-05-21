/**
 * 
 */
package org.minnal.instrument.entity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.javalite.common.Inflector;
import org.minnal.core.util.Node;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaDataProvider;
import org.minnal.instrument.entity.metadata.ParameterMetaData;

/**
 * @author ganeshs
 *
 */
public class EntityNode extends Node<EntityNode, EntityNodePath, EntityMetaData> {

	private String name;
	
	private String resourceName;
	
	private Map<Class<?>, List<String>> visitedEntities = new HashMap<Class<?>, List<String>>();
	
	public EntityNode(Class<?> entityClass) {
		this(entityClass, Inflector.camelize(Inflector.underscore(entityClass.getSimpleName()), false));
	}
	
	public EntityNode(Class<?> entityClass, String name) {
		super(EntityMetaDataProvider.instance().getEntityMetaData(entityClass));
		this.name = name;
		this.resourceName = Inflector.tableize(name);
	}
	
	public void construct() {
		for (CollectionMetaData collection : getValue().getCollections()) {
			if (! collection.isEntity()) {
				continue;
			}
			EntityNode child = new EntityNode(collection.getElementType(), Inflector.singularize(collection.getName()));
			if (addChild(child) != null) {
				child.construct();
			}
		}
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	@Override
	protected boolean visited(EntityNode node) {
		List<String> associations = visitedEntities.get(node.getValue().getEntityClass());
		if (associations == null) {
			return false;
		}
		return associations.contains(node.getName());
	}
	
	@Override
	protected void markVisited(EntityNode node) {
		List<String> associations = visitedEntities.get(node.getValue().getEntityClass());
		if (associations == null) {
			associations = new ArrayList<String>();
			visitedEntities.put(node.getValue().getEntityClass(), associations);
		}
		associations.add(node.getName());
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the entityMetaData
	 */
	public EntityMetaData getEntityMetaData() {
		return getValue();
	}

	@Override
	protected EntityNode getThis() {
		return this;
	}
	
	@Override
	protected EntityNodePath createNodePath(List<EntityNode> path) {
		return new EntityNodePath(path);
	}
	
	/**
	 * A path from the root node to a leaf node in the entity hierarchy. The path will be used to construct the uris for single and bulk resources.
	 * It also identifies all the search fields marked using {@link Searchable} annotation in the entity hierarchy.
	 * 
	 * @author ganeshs
	 *
	 */
	public class EntityNodePath extends Node<EntityNode, EntityNodePath, EntityMetaData>.NodePath {
		
		private String bulkPath;
		
		private String singlePath;
		
		private List<String> searchParams = new ArrayList<String>();

		public EntityNodePath(List<EntityNode> path) {
			super(path);
			init(path);
		}
		
		private void init(List<EntityNode> path) {
			StringWriter writer = new StringWriter();
			Iterator<EntityNode> iterator = iterator();
			String prefix = "";
			EntityNode parent = null;
			while (iterator.hasNext()) {
				EntityNode node = iterator.next();
				String name = node.getResourceName();
				if (parent != null) {
					prefix = prefix.isEmpty() ? name : prefix + "." + name;
				}
				
				writer.append("/").append(name);
				if (iterator.hasNext()) {
					writer.append("/{" + node.getName() + "_id}");
				}
				
				addSearchFields(prefix, node);
				parent = node;
			}
			bulkPath = writer.toString();
			singlePath = bulkPath + "/{id}";
		}
		
		private void addSearchFields(String prefix, EntityNode node) {
			prefix = prefix.isEmpty() ? prefix : prefix + ".";
			for (ParameterMetaData meta : node.getEntityMetaData().getSearchFields()) {
				searchParams.add(prefix + Inflector.underscore(meta.getFieldName()));
			}
			for (AssociationMetaData meta : node.getEntityMetaData().getAssociations()) {
				if (meta.isEntity()) {
					String assocPrefix = prefix + Inflector.underscore(meta.getName()) + ".";
					EntityMetaData data = EntityMetaDataProvider.instance().getEntityMetaData(meta.getType());
					for (ParameterMetaData paramMeta : data.getSearchFields()) {
						searchParams.add(assocPrefix + Inflector.underscore(paramMeta.getFieldName()));
					}
				}
			}
		}
		
		public String getBulkPath() {
			return bulkPath;
		}
		
		public String getSinglePath() {
			return singlePath;
		}
		
		public String getName() {
			return get(size() - 1).getName();
		}

		/**
		 * @return the searchParams
		 */
		public List<String> getSearchParams() {
			return searchParams;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((singlePath == null) ? 0 : singlePath.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			EntityNodePath other = (EntityNodePath) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (singlePath == null) {
				if (other.singlePath != null)
					return false;
			} else if (!singlePath.equals(other.singlePath))
				return false;
			return true;
		}

		private EntityNode getOuterType() {
			return EntityNode.this;
		}

	}
}
