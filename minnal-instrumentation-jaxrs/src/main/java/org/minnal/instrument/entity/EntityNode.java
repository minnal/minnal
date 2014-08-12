/**
 * 
 */
package org.minnal.instrument.entity;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.javalite.common.Inflector;
import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaDataProvider;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.minnal.utils.Node;
import org.minnal.utils.reflection.ClassUtils;
import org.minnal.utils.route.QueryParam;
import org.minnal.utils.route.QueryParam.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Splitter;

/**
 * @author ganeshs
 *
 */
public class EntityNode extends Node<EntityNode, EntityNodePath, EntityMetaData> {

	private String name;
	
	private String resourceName;
	
	private Map<Class<?>, List<String>> visitedEntities = new HashMap<Class<?>, List<String>>();
	
	private CollectionMetaData source;
	
	private NamingStrategy namingStrategy;
	
	private static final Logger logger = LoggerFactory.getLogger(EntityNode.class);
	
	public EntityNode(Class<?> entityClass, NamingStrategy namingStrategy) {
		this(entityClass, namingStrategy.getEntityName(entityClass), namingStrategy);
	}
	
	public EntityNode(Class<?> entityClass, String name, NamingStrategy namingStrategy) {
		super(EntityMetaDataProvider.instance().getEntityMetaData(entityClass));
		this.name = name;
		this.resourceName = namingStrategy.getResourceName(name);
		this.namingStrategy = namingStrategy;
	}
	
	EntityNode(CollectionMetaData collection, NamingStrategy namingStrategy) {
		this(collection.getElementType(), namingStrategy.getEntityCollectionName(collection.getName()), namingStrategy);
		this.source = collection;
	}
	
	public void construct() {
		LinkedList<EntityNode> queue = new LinkedList<EntityNode>();
		queue.offer(this);
		
		while (! queue.isEmpty()) {
			EntityNode node = queue.poll();
			
			for (CollectionMetaData collection : node.getValue().getCollections()) {
				if (! collection.isEntity()) {
					continue;
				}
				EntityNode child = new EntityNode(collection, namingStrategy);
				if (node.addChild(child) != null) {
					queue.offer(child);
				}
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
		logger.debug("Marking the node {} as visited in this node {}", node, this);
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
	
	/**
	 * @return
	 */
	public CollectionMetaData getSource() {
		return source;
	}

	@Override
	protected EntityNode getThis() {
		return this;
	}
	
	@Override
	protected EntityNodePath createNodePath(List<EntityNode> path) {
		return new EntityNodePath(path);
	}
	
	public EntityNodePath getEntityNodePath(String path) {
		Iterable<String> nodes = null;
		if (! StringUtils.isBlank(path)) {
			nodes = Splitter.on(".").split(path);
		}
		List<EntityNode> list = new ArrayList<EntityNode>();

		EntityNode lastNode = this;
		if (nodes != null) {
			for (String node : nodes ) {
				list.add(lastNode);
				lastNode = lastNode.getChild(namingStrategy.getEntityCollectionName(node));
				if (lastNode == null) {
					logger.error("Invalid path - {} for the node - {}", path, getName());
					throw new MinnalInstrumentationException("Invalid path - " + path + " for the node - " + getName());
				}
			}
		}
		list.add(lastNode);
		return new EntityNodePath(list);
	}
	
	protected EntityNode getChild(String name) {
		name = namingStrategy.getResourceName(name);
		for (EntityNode child : getChildren()) {
			if (child.getResourceName().equals(name)) {
				return child;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		return "EntityNode [name=" + name + ", resourceName=" + resourceName + "]";
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
		
		private String name;
		
		private boolean createAllowed = true;
		
		private boolean readAllowed = true;
		
		private boolean updateAllowed = true;
		
		private boolean deleteAllowed = true;
		
		private List<QueryParam> queryParams = new ArrayList<QueryParam>();

		public EntityNodePath(List<EntityNode> path) {
			super(path);
			init(path);
			buildPath(path);
		}
		
		private void init(List<EntityNode> path) {
			if (path.size() == 1) {
				EntityMetaData data = path.get(0).getValue();
				AggregateRoot root = ClassUtils.getAnnotation(data.getEntityClass(), AggregateRoot.class);
				if (root != null) {
					createAllowed = root.create();
					readAllowed = root.read();
					updateAllowed = root.update();
					deleteAllowed = root.delete();
				}
			} else {
				EntityNode node = path.get(size() - 1);
				if (node.source != null) {
					createAllowed = node.source.isCreateAllowed();
					readAllowed = node.source.isReadAllowed();
					updateAllowed = node.source.isUpdateAllowed();
					deleteAllowed = node.source.isDeleteAllowed();
				}
			}
		}
		
		private void buildPath(List<EntityNode> path) {
			StringWriter writer = new StringWriter();
			Iterator<EntityNode> iterator = iterator();
			String prefix = "";
			EntityNode parent = null;
			StringWriter pathName = new StringWriter();
			while (iterator.hasNext()) {
				EntityNode node = iterator.next();
				String name = node.getResourceName();
				
				pathName.append(node.getName());
				writer.append("/").append(name);
				if (iterator.hasNext()) {
					writer.append("/{" + namingStrategy.getPathSegment(node.getName() + "Id") + "}");
					pathName.append("_");
				}
				
				if (! iterator.hasNext()) {
					if (parent != null) {
						prefix = prefix.isEmpty() ? name : prefix + "." + name;
					}
					addSearchFields(prefix, node);
				}
				parent = node;
			}
			bulkPath = writer.toString();
			singlePath = bulkPath + "/{id}";
			name = Inflector.camelize(pathName.toString());
		}
		
		private void addSearchFields(String prefix, EntityNode node) {
			QueryParam param = null;
			prefix = prefix.isEmpty() ? prefix : prefix + ".";
			for (ParameterMetaData meta : node.getEntityMetaData().getSearchFields()) {
				param = new QueryParam(prefix + namingStrategy.getQueryParamName(meta.getFieldName()), Type.typeOf(meta.getType()), "The " + meta.getFieldName());
				queryParams.add(param);
			}
			for (AssociationMetaData meta : node.getEntityMetaData().getAssociations()) {
				if (meta.isEntity()) {
					String assocPrefix = prefix + namingStrategy.getQueryParamName(meta.getName()) + ".";
					EntityMetaData data = EntityMetaDataProvider.instance().getEntityMetaData(meta.getType());
					for (ParameterMetaData paramMeta : data.getSearchFields()) {
						param = new QueryParam(assocPrefix + namingStrategy.getQueryParamName(paramMeta.getFieldName()), Type.typeOf(paramMeta.getType()), "The " + paramMeta.getFieldName());
						queryParams.add(param);
					}
				}
			}
			for (EntityNode child : node.getChildren()) {
				String collectionPrefix = prefix + child.getResourceName();
				addSearchFields(collectionPrefix, child);
			}
		}
		
		public String getBulkPath() {
			return bulkPath;
		}
		
		public String getSinglePath() {
			return singlePath;
		}
		
		public String getName() {
			return name;
		}

		/**
		 * @return the queryParams
		 */
		public List<QueryParam> getQueryParams() {
			return queryParams;
		}
		
		public boolean isCreateAllowed() {
			return createAllowed;
		}
		
		public boolean isReadAllowed() {
			return readAllowed;
		}
		
		public boolean isUpdateAllowed() {
			return updateAllowed;
		}
		
		public boolean isDeleteAllowed() {
			return deleteAllowed;
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
		
		@Override
		public String toString() {
			return getSinglePath();
		}

		private EntityNode getOuterType() {
			return EntityNode.this;
		}

	}
}
