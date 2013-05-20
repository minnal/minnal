/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import javax.persistence.ManyToMany;

/**
 * @author ganeshs
 *
 */
public class ManyToManyAnnotationHandler extends OneToManyAnnotationHandler {
	
	@Override
	public Class<?> getAnnotationType() {
		return ManyToMany.class;
	}
}
