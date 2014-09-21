/**
 * 
 */
package org.minnal.jpa.entity;

import javax.persistence.Converter;

import org.minnal.utils.scanner.AbstractScanner;
import org.minnal.utils.scanner.Scanner;

/**
 * @author ganeshs
 *
 */
public class ConverterScanner extends AbstractScanner implements Scanner<Class<?>> {

	public ConverterScanner(ClassLoader classLoader, String... packages) {
		super(classLoader, packages);
	}

	public ConverterScanner(String... packages) {
		super(packages);
	}

	@Override
	protected boolean match(Class<?> clazz) {
		return clazz.isAnnotationPresent(Converter.class);
	}

}
