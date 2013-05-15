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
public class EntityKeyAnnotationHandler extends AbstractAnnotationHandler {

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		String key = method.getName().startsWith("get") ? method.getName().substring(3) : null;
		
		if (key == null) {
			throw new IllegalArgumentException("Method - " + method.getName() + " is not a getter");
		}
		metaData.setEntityKey(key);
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
