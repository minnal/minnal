/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import javax.persistence.ManyToOne;

/**
 * @author ganeshs
 *
 */
public class ManyToOneAnnotationHandler extends OneToOneAnnotationHandler {
	
	@Override
	public Class<?> getAnnotationType() {
		return ManyToOne.class;
	}
}
