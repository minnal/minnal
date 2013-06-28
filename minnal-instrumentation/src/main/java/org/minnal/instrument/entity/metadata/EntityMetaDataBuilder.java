/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

import org.minnal.instrument.entity.metadata.handler.AbstractAnnotationHandler;
import org.minnal.instrument.util.ClassUtils;

import com.sun.tools.javac.util.List;

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
		LinkedList<Field> fieldList = ClassUtils.getAllFields(new LinkedList<Field>(), metaData.getEntityClass());
		for (Field field : fieldList) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				visitor.visitAnnotationField(annotation, field);
			}
		}
		
		 LinkedList<Method> methodList = ClassUtils.getAllMethods(new LinkedList<Method>(), metaData.getEntityClass());		
		for (Method method : methodList) {
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
