/**
 * 
 */
package org.minnal.instrument.resource;

import org.minnal.core.resource.ResourceClass;
import org.minnal.core.util.Node.PathVisitor;
import org.minnal.instrument.entity.EntityNode;
import org.minnal.instrument.entity.EntityNode.EntityNodePath;

/**
 * @author ganeshs
 *
 */
public class ResourceEnhancer implements PathVisitor<EntityNodePath, EntityNode> {
	
	private ResourceClass resource;
	
	private ResourceWrapper resourceWrapper;

	public ResourceEnhancer(ResourceClass resource) {
		this.resource = resource;
	}
	
	public void enhance() {
		resourceWrapper = createResourceWrapper(resource);
		EntityNode tree = new EntityNode(resource.getEntityClass());
		tree.construct();
		tree.traverse(this);
		resourceWrapper.wrap();
	}
	
	public void visit(EntityNodePath path) {
		resourceWrapper.addPath(path);
	}
	
	protected ResourceWrapper createResourceWrapper(ResourceClass resourceClass) {
		return new ResourceWrapper(resourceClass);
	}
}
