/**
 * 
 */
package org.minnal.instrument.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this if you have to annotation Secure multiple times over an element.
 * 
 * @see Secure
 * 
 * @author ganeshs
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecureMultiple {

	public Secure[] value();
	
}
