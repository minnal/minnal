/**
 * 
 */
package org.minnal.instrument.entity;

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
public @interface AggregateRoot {

	public boolean create() default true;
	
	public boolean read() default true;
	
	public boolean update() default true;
	
	public boolean delete() default true;
}
