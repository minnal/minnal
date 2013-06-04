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

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConfigurationProvider;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.route.RouteBuilder;
import org.minnal.core.route.Routes;
import org.minnal.core.server.exception.ApplicationException;
import org.minnal.core.server.exception.ExceptionHandler;
import org.minnal.core.util.Generics;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author ganeshs
 *
 */
public abstract class Application<T extends ApplicationConfiguration> implements Lifecycle {

	private List<Filter> filters = new ArrayList<Filter>();
	
	private Routes routes = new Routes();
	
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
	
	protected void addExceptionMapping(Class<? extends Exception> from, Class<? extends ApplicationException> to) {
		exceptionHandler.mapException(from, to);
	}
	
	protected void mapExceptions() {
	}
	
	protected void addResource(Class<?> resourceClass) {
		addResource(resourceClass, new ResourceConfiguration(resourceClass.getSimpleName(), configuration));
	}
	
	protected void addResource(Class<?> resourceClass, ResourceConfiguration resourceConfiguration) {
		resourceConfiguration.setParent(configuration);
		ResourceClass resource = new ResourceClass(resourceConfiguration, resourceClass);
		addResource(resource);
	}
	
	public void addResource(ResourceClass resourceClass) {
		resources.put(resourceClass.getResourceClass(), resourceClass);
	}

	/**
	 * @return the routes
	 */
	public Routes getRoutes() {
		return routes;
	}

	/**
	 * Will be used by the container to set the absolute path for this application
	 * 
	 * @return the path
	 */
	String getPath() {
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
	
	protected ResourceClass resource(Class<?> clazz) {
		if (! resources.containsKey(clazz)) {
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
	
	public boolean shouldInstrument() {
		return false;
	}

	@JsonValue
	@Override
	public String toString() {
		return configuration.getName();
	}
}
