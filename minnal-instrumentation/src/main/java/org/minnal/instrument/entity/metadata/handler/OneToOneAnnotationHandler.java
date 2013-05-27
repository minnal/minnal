/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;

/**
 * @author ganeshs
 *
 */
public class OneToOneAnnotationHandler extends AbstractAnnotationHandler {
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		String name = method.getName().startsWith("get") ? method.getName().substring(3) : method.getName();
		AssociationMetaData associationMetaData = new AssociationMetaData(toLowerCamelCase(name), method.getReturnType(), isEntity(method.getReturnType()));
		metaData.addAssociation(associationMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		String name = field.getName();
		AssociationMetaData associationMetaData = new AssociationMetaData(name, field.getType(), isEntity(field.getType()));
		metaData.addAssociation(associationMetaData);
	}
	
	private boolean isEntity(Class<?> entityClass) {
		return entityClass.isAnnotationPresent(Entity.class);
	}

	@Override
	public Class<?> getAnnotationType() {
		return OneToOne.class;
	}
}
