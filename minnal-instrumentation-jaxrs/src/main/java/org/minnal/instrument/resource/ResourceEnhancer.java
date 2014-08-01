/**
 * 
 */
package org.minnal.instrument.resource;

import org.minnal.core.util.Node.PathVisitor;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;
import org.minnal.instrument.resource.metadata.ResourceMetaData;

/**
 * @author ganeshs
 *
 */
public class ResourceEnhancer implements PathVisitor<EntityNodePath, EntityNode> {
	
	private ResourceMetaData resource;
	
	private Class<?> entityClass;
	
	private ResourceWrapper resourceWrapper;

	/**
	 * @param resource
	 * @param entityClass
	 */
	public ResourceEnhancer(ResourceMetaData resource, Class<?> entityClass) {
		this.resource = resource;
		this.entityClass = entityClass;
	}

	/**
	 * @param entityClass
	 */
	public ResourceEnhancer(Class<?> entityClass) {
		this(null, entityClass);
	}

	/**
	 * Enhances the resource an returns the enhanced class
	 * 
	 * @return
	 */
	public Class<?> enhance() {
		resourceWrapper = createResourceWrapper();
		EntityNode tree = new EntityNode(entityClass);
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
