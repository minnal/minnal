/**
 * 
 */
package org.minnal.instrument.resource;

import javax.ws.rs.Path;

import org.minnal.core.scanner.AbstractScanner;

/**
 * @author ganeshs
 *
 */
public class ResourceScanner extends AbstractScanner {

	/**
	 * @param packages
	 */
	public ResourceScanner(String... packages) {
		super(packages);
	}

	/**
	 * @param classLoader
	 * @param packages
	 */
	public ResourceScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}

	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.getAnnotation(Path.class) != null;
	}

}
