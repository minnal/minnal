/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import org.minnal.instrument.entity.metadata.handler.AbstractEntityAnnotationHandler;
import org.minnal.instrument.metadata.MetaDataBuilder;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataBuilder extends MetaDataBuilder<EntityMetaData, AbstractEntityAnnotationHandler>{
	
	/**
	 * @param entityClass
	 */
	public EntityMetaDataBuilder(Class<?> entityClass) {
		super(new EntityMetaData(entityClass));
	}

	@Override
	protected Class<?> getVistingClass() {
		return getMetaData().getEntityClass();
	}

}
