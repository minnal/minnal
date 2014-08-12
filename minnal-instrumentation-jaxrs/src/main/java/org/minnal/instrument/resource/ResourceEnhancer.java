/**
 * 
 */
package org.minnal.instrument.resource;

import org.minnal.instrument.NamingStrategy;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.utils.Node.PathVisitor;

/**
 * @author ganeshs
 *
 */
public class ResourceEnhancer implements PathVisitor<EntityNodePath, EntityNode> {
	
	private ResourceMetaData resource;
	
	private Class<?> entityClass;
	
	private ResourceWrapper resourceWrapper;
	
	private NamingStrategy namingStrategy;

	/**
	 * @param resource
	 * @param entityClass
	 */
	public ResourceEnhancer(ResourceMetaData resource, Class<?> entityClass, NamingStrategy namingStrategy) {
		this.resource = resource;
		this.entityClass = entityClass;
		this.namingStrategy = namingStrategy;
	}

	/**
	 * Enhances the resource an returns the enhanced class
	 * 
	 * @return
	 */
	public Class<?> enhance() {
		resourceWrapper = createResourceWrapper();
		EntityNode tree = new EntityNode(entityClass, namingStrategy);
		tree.construct();
		tree.traverse(this);
		return resourceWrapper.wrap();
	}
	
	public void visit(EntityNodePath path) {
		resourceWrapper.addPath(path);
	}
	
	/**
	 * Creates the resource wrapper
	 * 
	 * @return
	 */
	protected ResourceWrapper createResourceWrapper() {
		return new ResourceWrapper(resource, entityClass);
	}
}
