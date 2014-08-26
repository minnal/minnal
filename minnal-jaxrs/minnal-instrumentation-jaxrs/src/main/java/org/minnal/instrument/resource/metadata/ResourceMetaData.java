/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import java.util.HashSet;
import java.util.Set;

import org.minnal.instrument.metadata.MetaData;
import org.minnal.utils.route.RoutePattern;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaData extends MetaData {

	private String path;
	
	private RoutePattern pattern;
	
	private Set<ResourceMethodMetaData> resourceMethods = new HashSet<ResourceMethodMetaData>();
	
	private Set<ResourceMetaData> subResources = new HashSet<ResourceMetaData>();
	
	private Class<?> resourceClass;
	
	/**
	 * @param resourceClass
	 * @param path
	 */
	public ResourceMetaData(Class<?> resourceClass, String path) {
		super(resourceClass.getName());
		this.resourceClass = resourceClass;
		this.path = path;
		this.pattern = new RoutePattern(path);
	}
	
	/**
	 * Adds the method to the resource
	 * 
	 * @param method
	 */
	public void addResourceMethod(ResourceMethodMetaData method) {
		resourceMethods.add(method);
	}
	
	/**
	 * Adds the sub resources to the resource
	 * 
	 * @param subResource
	 */
	public void addSubResource(ResourceMetaData subResource) {
		subResources.add(subResource);
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return the resourceMethods
	 */
	public Set<ResourceMethodMetaData> getResourceMethods() {
		return resourceMethods;
	}
	
	/**
	 * Returns all the resource methods including the sub-resources of this resource
	 * 
	 * @return
	 */
	public Set<ResourceMethodMetaData> getAllResourceMethods() {
		Set<ResourceMethodMetaData> methods = Sets.newHashSet(resourceMethods);
		for (ResourceMetaData subResource : getSubResources()) {
			methods.addAll(subResource.getAllResourceMethods());
		}
		return methods;
	}

	/**
	 * @return the subResources
	 */
	public Set<ResourceMetaData> getSubResources() {
		return subResources;
	}

	/**
	 * @return the resourceClass
	 */
	public Class<?> getResourceClass() {
		return resourceClass;
	}

	/**
	 * @return the pattern
	 */
	public RoutePattern getPattern() {
		return pattern;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((resourceClass == null) ? 0 : resourceClass.hashCode());
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
		ResourceMetaData other = (ResourceMetaData) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (resourceClass == null) {
			if (other.resourceClass != null)
				return false;
		} else if (!resourceClass.equals(other.resourceClass))
			return false;
		return true;
	}
}
