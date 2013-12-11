/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Embedded;

import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ValueMetaData;

/**
 * @author ganeshs
 *
 */
public class EmbeddedAnnotationHandler extends AbstractAnnotationHandler {
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		ValueMetaData valueMetaData = new ValueMetaData(getGetterName(method, false), method.getReturnType());
		metaData.addValue(valueMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		ValueMetaData valueMetaData = new ValueMetaData(field.getName(), field.getType());
		metaData.addValue(valueMetaData);
	}

	@Override
	public Class<?> getAnnotationType() {
		return Embedded.class;
	}
}
