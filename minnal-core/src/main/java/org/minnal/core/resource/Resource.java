/**
 * 
 */
package org.minnal.core.resource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.activejpa.entity.Model;

/**
 * Marks the type as a resource and associates it with a root entity
 * 
 * @author ganeshs
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Resource {
	
	/**
	 * The entity class that this resource manages
	 * 
	 * @return
	 */
	Class<? extends Model> value();
}
