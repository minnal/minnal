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
		String name = method.getName().startsWith("get") ? method.getName().substring(3) : method.getName();
		Class<?> elementType = getElementType(method.getGenericReturnType());
		CollectionMetaData collectionMetaData = new CollectionMetaData(name, elementType, method.getReturnType(), isEntity(elementType));
		metaData.addCollection(collectionMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		String name = field.getName();
		Class<?> elementType = getElementType(field.getGenericType());
		CollectionMetaData collectionMetaData = new CollectionMetaData(name, elementType, field.getType(), isEntity(elementType));
		metaData.addCollection(collectionMetaData);
	}
	
	private Class<?> getElementType(Type type) {
		if (type instanceof ParameterizedType) {
			Class<?> rawType = (Class<?>) ((ParameterizedType) type).getRawType();
			if (Collection.class.isAssignableFrom(rawType)) {
				return (Class<?>) ((ParameterizedType)type).getActualTypeArguments()[0];
			} else if (Map.class.isAssignableFrom(rawType)){
				return (Class<?>) ((ParameterizedType)type).getActualTypeArguments()[1];
			}
		}	
		return Object.class;
	}
	
	private boolean isEntity(Class<?> entityClass) {
		return entityClass.isAnnotationPresent(Entity.class);
	}

	@Override
	public Class<?> getAnnotationType() {
		return OneToMany.class;
	}
}
