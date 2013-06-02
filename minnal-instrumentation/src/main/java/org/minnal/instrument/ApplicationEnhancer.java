/**
 * 
 */
package org.minnal.instrument;

import java.util.ArrayList;
import java.util.List;

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
		
		for (ResourceClass resource : application.getResources()) {
			resources.remove(resource.getResourceClass());
			if (resource.getEntityClass() != null) {
				entities.remove(resource.getEntityClass());
				createEnhancer(resource).enhance();
			}
		}
		
		for (Class<?> resource : resources) {
			ResourceClass resourceClass = new ResourceClass(new ResourceConfiguration(resource.getSimpleName(), application.getConfiguration()), resource);
			if (resourceClass.getEntityClass() != null) {
				entities.remove(resourceClass.getEntityClass());
			}
			createEnhancer(resourceClass).enhance();
			application.addResource(resourceClass);
		}
		
		for (Class<?> entity : entities) {
			ResourceClass resourceClass = new ResourceClass(entity, new ResourceConfiguration(entity.getSimpleName(), application.getConfiguration()));
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
}
