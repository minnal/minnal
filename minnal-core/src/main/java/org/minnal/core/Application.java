/**
 * 
 */
package org.minnal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.common.Inflector;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConfigurationProvider;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.Routes;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.utils.reflection.Generics;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author ganeshs
 *
 */
public abstract class Application<T extends ApplicationConfiguration> implements Lifecycle {

	private List<Filter> filters = new ArrayList<Filter>();
	
	private Map<ResourceClass, Routes> routes = new HashMap<ResourceClass, Routes>();
	
	private Map<Class<?>, ResourceClass> resources = new HashMap<Class<?>, ResourceClass>();
	
	private String path;
	
	private T configuration;
	
	private List<Plugin> plugins = new ArrayList<Plugin>();
	
	private ConfigurationProvider configurationProvider = ConfigurationProvider.getDefault();
	
	private ExceptionHandler exceptionHandler = new ExceptionHandler();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Application() {
		this.configuration = (T) configurationProvider.provide((Class)Generics.getTypeParameter(getClass(), ApplicationConfiguration.class));
	}
	
	public Application(T configuration) {
		this.configuration = configuration;
	}
	
	public ExceptionHandler getExceptionHandler() {
		return exceptionHandler;
	}
	
	public void addFilter(Filter filter) {
		filters.add(filter);
	}
	
	public void init() {
		registerPlugins();
		addFilters();
		defineResources();
		defineRoutes();
		
		for (Plugin plugin : plugins) {
			plugin.init(this);
		}
	}
	
	public void start() {
		for (ResourceClass resource : getResources()) {
			Routes routes = this.routes.get(resource);
			if (routes == null) {
				routes = new Routes();
				this.routes.put(resource, routes);
			}
			
			for (RouteBuilder builder : resource.getRouteBuilders()) {
				routes.addRoute(builder);
			}
		}
	}
	
	public void stop() {
		for (Plugin plugin : plugins) {
			plugin.destroy();
		}
	}
	
	protected abstract void registerPlugins();
	
	protected abstract void addFilters();
	
	protected abstract void defineRoutes();
	
	protected abstract void defineResources();
	
	protected void mapExceptions() {
	}
	
	public void addExceptionMapping(Class<? extends Exception> from, Class<? extends ApplicationException> to) {
		exceptionHandler.mapException(from, to);
	}
	
	public void addResource(Class<?> resourceClass) {
		addResource(resourceClass, "/");
	}
	
	public void addResource(Class<?> resourceClass, String basePath) {
		addResource(resourceClass, new ResourceConfiguration(getResourceName(resourceClass), configuration), basePath);
	}
	
	public void addResource(Class<?> resourceClass, ResourceConfiguration resourceConfiguration) {
		addResource(resourceClass, resourceConfiguration, "/");
	}
	
	public void addResource(Class<?> resourceClass, ResourceConfiguration resourceConfiguration, String basePath) {
		resourceConfiguration.setParent(configuration);
		ResourceClass resource = new ResourceClass(resourceConfiguration, resourceClass, basePath);
		addResource(resource);
	}
	
	public void addResource(ResourceClass resourceClass) {
		if (resourcePathExists(resourceClass.getBasePath())) {
			throw new MinnalException("Resource Path - " + resourceClass.getBasePath() + " already exists in the application");
		}
		resources.put(resourceClass.getResourceClass(), resourceClass);
	}
	
	private boolean resourcePathExists(String path) {
		for (ResourceClass clazz : resources.values()) {
			if (clazz.getBasePath().equals(path)) {
				return true;
			}
		}
		return false;
	}
	
	public Routes getRoutes(ResourceClass clazz) {
		return routes.get(clazz);
	}

	/**
	 * Will be used by the container to set the absolute path for this application
	 * 
	 * @return the path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Will be used by the container to set the absolute path for this application
	 * 
	 * @param path the path to set
	 */
	void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * If a resource class for the given class is found, returns the resource class. If not found, and if a sub class for the given class
	 * exists, returns that. Else throws an exception
	 * 
	 * @param clazz
	 * @return
	 */
	public ResourceClass resource(Class<?> clazz) {
		if (! resources.containsKey(clazz)) {
			for (ResourceClass resourceClass : resources.values()) {
				if (clazz.isAssignableFrom(resourceClass.getResourceClass())) {
					return resourceClass;
				}
			}
			throw new MinnalException("Resource - " + clazz.getName() + " not found");
		}
		return resources.get(clazz);
	}
	
	/**
	 * Returns all the resources managed by this application
	 * 
	 * @return
	 */
	public Collection<ResourceClass> getResources() {
		return resources.values();
	}

	/**
	 * @return the configuration
	 */
	public T getConfiguration() {
		return configuration;
	}
	
	public void registerPlugin(Plugin plugin) {
		plugins.add(plugin);
	}
	
	/**
	 * @return the filters
	 */
	public List<Filter> getFilters() {
		return Collections.unmodifiableList(filters);
	}
	
	@Deprecated
	public boolean shouldInstrument() {
		return false;
	}

	@JsonValue
	@Override
	public String toString() {
		return configuration.getName();
	}
	
	private String getResourceName(Class<?> resourceClass) {
		String resourceName = resourceClass.getSimpleName();
		if (resourceName.toLowerCase().endsWith("resource")) {
			resourceName = resourceName.substring(0, resourceName.toLowerCase().indexOf("resource"));
		}
		return Inflector.tableize(resourceName);
	}
}
