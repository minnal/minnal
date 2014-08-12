/**
 * 
 */
package org.minnal.instrument;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.Application;

import org.minnal.instrument.entity.AggregateRootScanner;
import org.minnal.instrument.resource.PathScanner;
import org.minnal.instrument.resource.ResourceEnhancer;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.scanner.Scanner;
import org.minnal.utils.scanner.Scanner.Listener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationEnhancer {

	private Application application;
	
	private NamingStrategy namingStrategy;
	
	private String[] entityPackages;
	
	private String[] resourcePackages;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationEnhancer.class);
	
	/**
	 * @param application
	 * @param namingStrategy
	 * @param entityPackages
	 */
	public ApplicationEnhancer(Application application, NamingStrategy namingStrategy, String[] entityPackages, String[] resourcePackages) {
		this.application = application;
		this.namingStrategy = namingStrategy;
		this.entityPackages = entityPackages;
		this.resourcePackages = resourcePackages;
	}
	
	/**
	 * @return the application
	 */
	protected Application getApplication() {
		return application;
	}

	/**
	 * Enhances the application
	 */
	public void enhance() {
		List<Class<?>> entities = scanEntities();
		List<Class<?>> resources = scanResources();
		
		for (Class<?> resourceClass : resources) {
			ResourceMetaData resource = ResourceMetaDataProvider.instance().getResourceMetaData(resourceClass);
			String path = HttpUtil.getRootSegment(resource.getPath());
			for (Class<?> entityClass : entities) {
				String rootPath = HttpUtil.structureUrl(namingStrategy.getResourceName(entityClass));
				if (path.equals(rootPath)) {
					entities.remove(entityClass);
					addResource(createEnhancer(resource, entityClass).enhance());
					break;
				}
			}
		}
		
		for (Class<?> entityClass : entities) {
			addResource(createEnhancer(null, entityClass).enhance());
		}
	}
	
	/**
	 * @param resourceClass
	 */
	protected void addResource(Class<?> resourceClass) {
		application.getClasses().add(resourceClass);
	}
	
	/**
	 * Returns the entities defined under the entity packages
	 * 
	 * @return
	 */
	protected List<Class<?>> scanEntities() {
		return scanClasses(new AggregateRootScanner(entityPackages));
	}
	
	/**
	 * Returns the resources defined under the resource packages
	 * 
	 * @return
	 */
	protected List<Class<?>> scanResources() {
		return scanClasses(new PathScanner(resourcePackages));
	}
	
	/**
	 * Scan the classes using the scanner
	 * 
	 * @param scanner
	 * @return
	 */
	protected List<Class<?>> scanClasses(Scanner<Class<?>> scanner) {
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		scanner.scan(new Listener<Class<?>>() {
			public void handle(Class<?> t) {
				classes.add(t);
			}
		});
		return classes;
	}
	
	/**
	 * Creates the resource enhancer
	 * 
	 * @param resource
	 * @param entityClass
	 * @return
	 */
	protected ResourceEnhancer createEnhancer(ResourceMetaData resource, Class<?> entityClass) {
		return new ResourceEnhancer(resource, entityClass, namingStrategy);
	}
}
