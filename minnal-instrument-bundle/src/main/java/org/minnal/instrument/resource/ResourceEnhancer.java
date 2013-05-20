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
		this.resourceWrapper = new ResourceWrapper(resource);
	}
	
	public ResourceEnhancer(ResourceWrapper wrapper) {
		this.resourceWrapper = wrapper;
		this.resource = wrapper.getResourceClass();
	}
	
	public void enhance() {
		EntityNode tree = new EntityNode(resource.getEntityClass());
		tree.construct();
		tree.traverse(this);
		resourceWrapper.wrap();
	}
	
	public void visit(EntityNodePath path) {
		resourceWrapper.addPath(path);
	}
}
