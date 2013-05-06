/**
 * 
 */
package org.minnal.instrument.entity;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.atteo.evo.inflector.English;
import org.minnal.core.util.Node;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaDataProvider;

import com.google.common.base.CaseFormat;

/**
 * @author ganeshs
 *
 */
public class EntityNode extends Node<EntityNode, EntityNodePath> {

	private String name;
	
	private EntityMetaData entityMetaData;
	
	public EntityNode(Class<?> entityClass) {
		this.name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, English.plural(entityClass.getSimpleName()));
		this.entityMetaData = EntityMetaDataProvider.instance().getEntityMetaData(entityClass);
		populateChildren();
	}

	private void populateChildren() {
		for (CollectionMetaData collection : entityMetaData.getCollections()) {
			if (! collection.isEntity()) {
				continue;
			}
			addChild(new EntityNode(collection.getElementType()));
		}
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
		return entityMetaData;
	}

	@Override
	protected EntityNode getThis() {
		return this;
	}
	
	@Override
	protected EntityNodePath createNodePath(List<EntityNode> path) {
		return new EntityNodePath(path);
	}
	
	public class EntityNodePath extends Node<EntityNode, EntityNodePath>.NodePath {
		
		private String bulkPath;
		
		private String singlePath;

		public EntityNodePath(List<EntityNode> path) {
			super(path);
			init(path);
		}
		
		private void init(List<EntityNode> path) {
			StringWriter writer = new StringWriter();
			Iterator<EntityNode> iterator = iterator();
			while (iterator.hasNext()) {
				EntityNode node = iterator.next();
				writer.append("/").append(node.getName());
				if (iterator.hasNext()) {
					writer.append("/{" + node.getName() + "_id}");
				}
			}
			bulkPath = writer.toString();
			singlePath = bulkPath + "/{id}";
		}
		
		public String getBulkPath() {
			return bulkPath;
		}
		
		public String getSinglePath() {
			return singlePath;
		}
		
		public String getName() {
			return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, get(size() - 1).getName());
		}

	}
}
