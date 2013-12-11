/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.metadata.handler.AbstractAnnotationHandler;
import org.minnal.utils.reflection.ClassUtils;


/**
 * @author ganeshs
 *
 */
public class ValueMetaDataBuilder {
	
	private ValueMetaData metaData;
	
	public ValueMetaDataBuilder(String name, Class<?> valueClass) {
		this.metaData = new ValueMetaData(name, valueClass);
	}

	public ValueMetaData build() {
		ValueVisitor visitor = new ValueVisitor();
		accept(visitor);
		return metaData;
	}

	private void accept(ValueVisitor visitor) {
		for (Field field : ClassUtils.getAllFields(metaData.getType())) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				visitor.visitAnnotationField(annotation, field);
			}
		}

		for (Method method : ClassUtils.getAllMethods(metaData.getType())) {
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
	private class ValueVisitor {

		public void visitAnnotationMethod(Annotation annotation, Method method) {
			AbstractAnnotationHandler handler = AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
//				handler.handle(metaData, annotation, method);
			}
		}
		
		public void visitAnnotationField(Annotation annotation, Field field) {
			AbstractAnnotationHandler handler = AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
//				handler.handle(metaData, annotation, field);
			}
		}
	}
}
