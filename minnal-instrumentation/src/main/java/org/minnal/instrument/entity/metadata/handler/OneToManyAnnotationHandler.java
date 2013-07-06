/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.minnal.instrument.entity.metadata.CollectionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;

/**
 * @author ganeshs
 *
 */
public class OneToManyAnnotationHandler extends AbstractAnnotationHandler {
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		Class<?> elementType = getElementType(method.getGenericReturnType());
		CollectionMetaData collectionMetaData = new CollectionMetaData(getGetterName(method, false), 
				elementType, method.getReturnType(), isEntity(elementType));
		metaData.addCollection(collectionMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		String name = field.getName();
		Class<?> elementType = getElementType(field.getGenericType());
		CollectionMetaData collectionMetaData = new CollectionMetaData(name, elementType, field.getType(), isEntity(elementType));
		metaData.addCollection(collectionMetaData);
	}
	
	@Override
	public Class<?> getAnnotationType() {
		return OneToMany.class;
	}
}
