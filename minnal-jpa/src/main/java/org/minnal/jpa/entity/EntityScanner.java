/**
 * 
 */
package org.minnal.jpa.entity;

import javax.persistence.Entity;

import org.minnal.core.scanner.AbstractScanner;
import org.minnal.core.scanner.Scanner;

/**
 * @author ganeshs
 *
 */
public class EntityScanner extends AbstractScanner implements Scanner<Class<?>> {

	public EntityScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}

	public EntityScanner(String... packages) {
		super(packages);
	}

	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.isAnnotationPresent(Entity.class);
	}

}
