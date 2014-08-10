/**
 * 
 */
package org.minnal.instrument.resource;

import javax.ws.rs.Path;

import org.minnal.utils.scanner.AbstractScanner;

/**
 * @author ganeshs
 *
 */
public class PathScanner extends AbstractScanner {

	/**
	 * @param packages
	 */
	public PathScanner(String... packages) {
		super(packages);
	}

	/**
	 * @param classLoader
	 * @param packages
	 */
	public PathScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}

	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.getAnnotation(Path.class) != null;
	}

}
