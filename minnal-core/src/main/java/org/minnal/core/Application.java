/**
 * 
 */
package org.minnal.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.ext.ExceptionMapper;

import org.glassfish.jersey.server.ResourceConfig;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ConfigurationProvider;
import org.minnal.utils.reflection.Generics;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

/**
 * @author ganeshs
 *
 */
public abstract class Application<T extends ApplicationConfiguration> implements Lifecycle {

	private URI path;
	
	private T configuration;
	
	private List<Plugin> plugins = new ArrayList<Plugin>();
	
	private ConfigurationProvider configurationProvider = ConfigurationProvider.getDefault();
	
	private ResourceConfig resourceConfig;
	
	private ObjectMapper objectMapper = new ObjectMapper();
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Application() {
		this.configuration = (T) configurationProvider.provide((Class)Generics.getTypeParameter(getClass(), ApplicationConfiguration.class));
		resourceConfig =  new ResourceConfig();
	}
	
	public Application(T configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * @return the resourceConfig
	 */
	public ResourceConfig getResourceConfig() {
		return resourceConfig;
	}
	
	/**
	 * @param filter
	 */
	public void addFilter(Class<?> filter) {
		resourceConfig.register(filter);
	}
	
	/**
	 * @param filter
	 */
	public void addFilter(Object filter) {
		resourceConfig.register(filter);
	}
	
	/**
	 * @param mapper
	 */
	@SuppressWarnings("rawtypes")
	public void addExceptionMapper(Class<? extends ExceptionMapper> mapper) {
		resourceConfig.register(mapper);
	}
	
	/**
	 * @param mapper
	 */
	@SuppressWarnings("rawtypes")
	public void addExceptionMapper(ExceptionMapper mapper) {
		resourceConfig.register(mapper);
	}
	
	/**
	 * @return the objectMapper
	 */
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	/**
	 * @param objectMapper the objectMapper to set
	 */
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void init() {
		registerPlugins();
		addFilters();
		defineResources();
		mapExceptions();
		
		resourceConfig.register(new JacksonJaxbJsonProvider(objectMapper, null));
		
		for (Plugin plugin : plugins) {
			plugin.init(this);
		}
	}
	
	public void start() {
	}
	
	public void stop() {
		for (Plugin plugin : plugins) {
			plugin.destroy();
		}
	}
	
	protected abstract void registerPlugins();
	
	protected abstract void addFilters();
	
	protected abstract void defineResources();
	
	protected void mapExceptions() {
	}
	
	public void addResource(Class<?> resourceClass) {
		resourceConfig.register(resourceClass);
	}
	
	/**
	 * Will be used by the container to set the absolute path for this application
	 * 
	 * @return the path
	 */
	public URI getPath() {
		return path;
	}

	/**
	 * Will be used by the container to set the absolute path for this application
	 * 
	 * @param path the path to set
	 */
	void setPath(URI path) {
		this.path = path;
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
	
	@JsonValue
	@Override
	public String toString() {
		return configuration.getName();
	}
}
