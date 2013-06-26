/**
 * 
 */
package org.minnal.instrument;

import java.util.ArrayList;
import java.util.List;

import org.javalite.common.Inflector;
import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.ResourceConfiguration;
import org.minnal.core.resource.ResourceClass;
import org.minnal.core.scanner.Scanner;
import org.minnal.core.scanner.Scanner.Listener;
import org.minnal.instrument.entity.AggregateRootScanner;
import org.minnal.instrument.resource.ResourceEnhancer;
import org.minnal.instrument.resource.ResourceScanner;

/**
 * @author ganeshs
 * 
 */
public class ApplicationEnhancer {
	
	private Application<ApplicationConfiguration> application;
	
	public ApplicationEnhancer(Application<ApplicationConfiguration> application) {
		this.application = application;
	}
	
	public void enhance() {
		List<Class<?>> entities = getScannedClasses(new AggregateRootScanner(application.getConfiguration().getPackagesToScan().toArray(new String[0])));
		List<Class<?>> resources = getScannedClasses(new ResourceScanner(application.getConfiguration().getPackagesToScan().toArray(new String[0])));
		
		/**
		 *  1. Get the resources defined in the application via defineResources
		 *  2. If the resources has @Resource annotation defined, get the aggregate root from the annotation and remove it from the scanned entities
		 *  3. Remove the resources from the scanned classes marked with @Resource annotation
		 */
		for (ResourceClass resource : application.getResources()) {
			resources.remove(resource.getResourceClass());
			if (resource.getEntityClass() != null) {
				entities.remove(resource.getEntityClass());
				createEnhancer(resource).enhance();
			}
		}
		
		for (Class<?> resource : resources) {
			ResourceClass resourceClass = new ResourceClass(new ResourceConfiguration(getResourceName(resource), application.getConfiguration()), resource);
			if (resourceClass.getEntityClass() != null) {
				entities.remove(resourceClass.getEntityClass());
			}
			createEnhancer(resourceClass).enhance();
			application.addResource(resourceClass);
		}
		
		for (Class<?> entity : entities) {
			ResourceClass resourceClass = new ResourceClass(entity, new ResourceConfiguration(Inflector.tableize(entity.getSimpleName()), application.getConfiguration()));
			createEnhancer(resourceClass).enhance();
			application.addResource(resourceClass);
		}
	}
	
	protected ResourceEnhancer createEnhancer(ResourceClass resourceClass) {
		return new ResourceEnhancer(resourceClass);
	}
	
	protected List<Class<?>> getScannedClasses(Scanner<Class<?>> scanner) {
		final List<Class<?>> classes = new ArrayList<Class<?>>();
		if (! application.getConfiguration().getPackagesToScan().isEmpty()) {
			scanner.scan(new Listener<Class<?>>() {
				public void handle(Class<?> t) {
					classes.add(t);
				}
			});
		}
		return classes;
	}
	
	private String getResourceName(Class<?> resourceClass) {
		String resourceName = resourceClass.getSimpleName();
		if (resourceName.toLowerCase().endsWith("resource")) {
			resourceName = resourceName.substring(0, resourceName.toLowerCase().indexOf("resource"));
		}
		return Inflector.tableize(resourceName);
	}
}
