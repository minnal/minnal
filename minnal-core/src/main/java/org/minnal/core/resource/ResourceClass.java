/**
 * 
 */
package org.minnal.core.resource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.javalite.common.Inflector;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.route.RouteBuilder;
import org.minnal.utils.http.HttpUtil;

/**
 * @author ganeshs
 *
 */
public class ResourceClass {
	
	private String basePath = "/";

	private Class<?> resourceClass;
	
	private ResourceConfiguration configuration;
	
	private Class<?> entityClass;
	
	private List<RouteBuilder> routeBuilders = new ArrayList<RouteBuilder>();
	
	public ResourceClass(ResourceConfiguration configuration, Class<?> resourceClass, String basePath) {
		this.configuration = configuration;
		setResourceClass(resourceClass);
		this.basePath = HttpUtil.structureUrl(basePath);
	}
	
	public ResourceClass(ResourceConfiguration configuration, Class<?> resourceClass) {
		this(configuration, resourceClass, constructBasePath(getEntityClass(resourceClass)));
	}
	
	public ResourceClass(Class<?> entityClass, ResourceConfiguration configuration, String basePath) {
		this.configuration = configuration;
		this.entityClass = entityClass;
		this.basePath = HttpUtil.structureUrl(basePath);
	}
	
	public ResourceClass(Class<?> entityClass, ResourceConfiguration configuration) {
		this(entityClass, configuration, constructBasePath(entityClass));
	}
	
	private void validate(Class<?> resourceClass) {
		entityClass = getEntityClass(resourceClass);
	}
	
	private static Class<?> getEntityClass(Class<?> resourceClass) {
		Resource annotation = resourceClass.getAnnotation(Resource.class);
		if (annotation == null) {
			return null;
		}
		return annotation.value();
	}
	
	private static String constructBasePath(Class<?> entityClass) {
		if (entityClass == null) {
			return "/";
		}
		return HttpUtil.SEPARATOR + Inflector.tableize(entityClass.getSimpleName());
	}

	public Class<?> getResourceClass() {
		return resourceClass;
	}

	/**
	 * @return the entityClass
	 */
	public Class<?> getEntityClass() {
		return entityClass;
	}

	/**
	 * @return the configuration
	 */
	public ResourceConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(ResourceConfiguration configuration) {
		this.configuration = configuration;
	}
	
	public RouteBuilder builder(String path) {
		RouteBuilder builder = new RouteBuilder(this, basePath + HttpUtil.structureUrl(path));
		routeBuilders.add(builder);
		return builder;
	}

	/**
	 * @return the routeBuilders
	 */
	public List<RouteBuilder> getRouteBuilders() {
		return Collections.unmodifiableList(routeBuilders);
	}
	
	/**
	 * Checks if the resource already has a route with the given method and path
	 * 
	 * @param path
	 * @param method
	 * @return
	 */
	public boolean hasRoute(String path, HttpMethod method) {
		RouteBuilder builder = new RouteBuilder(this, path);
		int index = routeBuilders.indexOf(builder);
		if (index == -1) {
			return false;
		}
		return routeBuilders.get(index).supportedMethods().contains(method);
	}

	/**
	 * @param resourceClass the resourceClass to set
	 */
	public void setResourceClass(Class<?> resourceClass) {
		this.resourceClass = resourceClass;
		validate(resourceClass);
	}
	
	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((resourceClass == null) ? 0 : resourceClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResourceClass other = (ResourceClass) obj;
		if (resourceClass == null) {
			if (other.resourceClass != null)
				return false;
		} else if (!resourceClass.equals(other.resourceClass))
			return false;
		return true;
	}
}
