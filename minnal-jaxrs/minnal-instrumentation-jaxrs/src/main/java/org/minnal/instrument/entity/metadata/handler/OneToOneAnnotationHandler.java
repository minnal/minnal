/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.OneToOne;

import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;

/**
 * @author ganeshs
 *
 */
public class OneToOneAnnotationHandler extends AbstractEntityAnnotationHandler {
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		AssociationMetaData associationMetaData = new AssociationMetaData(getGetterName(method, false), 
				method.getReturnType(), isEntity(method.getReturnType()));
		metaData.addAssociation(associationMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		AssociationMetaData associationMetaData = new AssociationMetaData(field.getName(), field.getType(), isEntity(field.getType()));
		metaData.addAssociation(associationMetaData);
	}

	@Override
	public Class<?> getAnnotationType() {
		return OneToOne.class;
	}
}
