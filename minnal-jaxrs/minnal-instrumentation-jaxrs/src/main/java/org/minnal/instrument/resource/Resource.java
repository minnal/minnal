/**
 * 
 */
package org.minnal.instrument.resource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author ganeshs
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Resource {

	/**
	 * The path this resource maps to
	 *  
	 * @return
	 */
	public String value();
}
