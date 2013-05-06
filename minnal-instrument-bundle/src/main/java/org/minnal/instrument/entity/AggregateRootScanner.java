/**
 * 
 */
package org.minnal.instrument.entity;

import org.minnal.instrument.scanner.AbstractScanner;

/**
 * @author ganeshs
 *
 */
public class AggregateRootScanner extends AbstractScanner {
	
	public AggregateRootScanner(String... packages) {
		super(packages);
	}
	
	public AggregateRootScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}
	
	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.getAnnotation(AggregateRoot.class) != null;
	}

}
