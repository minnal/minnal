/**
 * 
 */
package org.minnal.instrument.resource;

import org.minnal.core.resource.Resource;
import org.minnal.instrument.scanner.AbstractScanner;

/**
 * @author ganeshs
 *
 */
public class ResourceScanner extends AbstractScanner {
	
	public ResourceScanner(String... packages) {
		super(packages);
	}
	
	public ResourceScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}
	
	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.getAnnotation(Resource.class) != null;
	}

}