/**
 * 
 */
package org.minnal.instrument;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import org.minnal.core.scanner.Scanner;
import org.minnal.core.scanner.Scanner.Listener;
import org.minnal.instrument.entity.AggregateRootScanner;
import org.minnal.instrument.resource.ResourceEnhancer;
import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMetaDataProvider;
import org.minnal.utils.http.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class ApplicationEnhancer {

	private Application application;
	
	private NamingStrategy namingStrategy;
	
	private String[] entityPackages;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationEnhancer.class);
	
	/**
	 * @param application
	 * @param namingStrategy
	 * @param entityPackages
	 */
	public ApplicationEnhancer(Application application, NamingStrategy namingStrategy, String[] entityPackages) {
		this.application = application;
		this.namingStrategy = namingStrategy;
		this.entityPackages = entityPackages;
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
		List<Class<?>> entities = scanClasses(new AggregateRootScanner(entityPackages));
		Set<ResourceMetaData> resources = getDefinedResources();
		
		for (ResourceMetaData resource : resources) {
			String path = HttpUtil.getRootSegment(resource.getPath());
			for (Class<?> entityClass : entities) {
				String rootPath = HttpUtil.structureUrl(namingStrategy.getResourceName(entityClass.getSimpleName()));
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
	 * Returns the defined resources from the application
	 * 
	 * @return
	 */
	protected Set<ResourceMetaData> getDefinedResources() {
		Set<Object> resources = Sets.newHashSet(application.getSingletons());
		resources.addAll(application.getClasses());
		return FluentIterable.from(resources).transform(new Function<Object, Class<?>>() {
			@Override
			public Class<?> apply(Object input) {
				return input instanceof Class ? (Class<?>) input : input.getClass();
			}
		}).filter(new Predicate<Class<?>>() {
			@Override
			public boolean apply(Class<?> input) {
				return org.minnal.utils.reflection.ClassUtils.hasAnnotation(input, Path.class);
			}
		}).transform(new Function<Class<?>, ResourceMetaData>() {
			@Override
			public ResourceMetaData apply(Class<?> input) {
				return ResourceMetaDataProvider.instance().getResourceMetaData(input);
			}
		}).toSet();
	}
	
	/**
	 * Creates the resource enhancer
	 * 
	 * @param resource
	 * @param entityClass
	 * @return
	 */
	protected ResourceEnhancer createEnhancer(ResourceMetaData resource, Class<?> entityClass) {
		return new ResourceEnhancer(resource, entityClass);
	}
}
