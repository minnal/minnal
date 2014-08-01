/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.EntityKey;
import org.minnal.instrument.entity.metadata.EntityMetaData;

/**
 * @author ganeshs
 *
 */
public class EntityKeyAnnotationHandler extends AbstractEntityAnnotationHandler {

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		metaData.setEntityKey(getGetterName(method, true));
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		metaData.setEntityKey(field.getName());
	}

	@Override
	public Class<?> getAnnotationType() {
		return EntityKey.class;
	}

}
