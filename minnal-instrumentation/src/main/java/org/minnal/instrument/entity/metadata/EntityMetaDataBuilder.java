/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.metadata.handler.AbstractAnnotationHandler;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataBuilder {
	
	private EntityMetaData metaData;
	
	public EntityMetaDataBuilder(Class<?> entityClass) {
		this.metaData = new EntityMetaData(entityClass);
	}

	public EntityMetaData build() {
		EntityVisitor visitor = new EntityVisitor();
		accept(visitor);
		return metaData;
	}
	
	private void accept(EntityVisitor visitor) {
		for (Field field : metaData.getEntityClass().getDeclaredFields()) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				visitor.visitAnnotationField(annotation, field);
			}
		}
		
		for (Method method : metaData.getEntityClass().getDeclaredMethods()) {
			Annotation[] annotations = method.getAnnotations();
			for (Annotation annotation : annotations) {
				visitor.visitAnnotationMethod(annotation, method);
			}
		}
	}
	
	/**
	 * Visitor that visits the annotations on methods and fields of the entity and handles them
	 * 
	 * @author ganeshs
	 */
	private class EntityVisitor {

		public void visitAnnotationMethod(Annotation annotation, Method method) {
			AbstractAnnotationHandler handler = AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
				handler.handle(metaData, annotation, method);
			}
		}
		
		public void visitAnnotationField(Annotation annotation, Field field) {
			AbstractAnnotationHandler handler = AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
				handler.handle(metaData, annotation, field);
			}
		}
	}
}
