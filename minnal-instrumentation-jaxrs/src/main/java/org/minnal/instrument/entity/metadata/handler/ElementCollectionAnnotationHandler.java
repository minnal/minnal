/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.ElementCollection;
import javax.persistence.OneToMany;

import org.minnal.instrument.entity.metadata.AssociationMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.utils.reflection.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ElementCollectionAnnotationHandler extends AbstractEntityAnnotationHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ElementCollectionAnnotationHandler.class); 

	@Override
	public Class<?> getAnnotationType() {
		return ElementCollection.class;
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		if (ClassUtils.hasAnnotation(method, OneToMany.class)) {
			logger.debug("Has OneToMany annotation as well. Leaving it to OneToManyAnnotationHandler to process");
			return;
		}
		Class<?> elementType = getElementType(method.getGenericReturnType());
		AssociationMetaData associationMetaData = new AssociationMetaData(getGetterName(method, false), elementType, isEntity(elementType));
		metaData.addAssociation(associationMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		if (ClassUtils.hasAnnotation(field, OneToMany.class)) {
			logger.debug("Has OneToMany annotation as well. Leaving it to OneToManyAnnotationHandler to process");
			return;
		}
		Class<?> elementType = getElementType(field.getGenericType());
		AssociationMetaData associationMetaData = new AssociationMetaData(field.getName(), elementType, isEntity(elementType));
		metaData.addAssociation(associationMetaData);
	}
}
