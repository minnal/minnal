/**
 * 
 */
package org.minnal.instrument.metadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.metadata.handler.AbstractAnnotationHandler;
import org.minnal.utils.reflection.ClassUtils;


/**
 * @author ganeshs
 *
 */
public abstract class MetaDataBuilder<T extends MetaData, H extends AbstractAnnotationHandler<T>> {
	
	private T metaData;
	
	/**
	 * @param metaData
	 */
	protected MetaDataBuilder(T metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the metaData
	 */
	public T getMetaData() {
		return metaData;
	}

	/**
	 * @return
	 */
	public T build() {
		Visitor visitor = new Visitor();
		accept(visitor);
		return metaData;
	}
	
	/**
	 * The class to be visited
	 * 
	 * @return
	 */
	protected abstract Class<?> getVistingClass();

	/**
	 * @param visitor
	 */
	private void accept(Visitor visitor) {
		for (Field field : ClassUtils.getAllFields(getVistingClass())) {
			Annotation[] annotations = field.getAnnotations();
			for (Annotation annotation : annotations) {
				visitor.visitAnnotationField(annotation, field);
			}
		}

		for (Method method : ClassUtils.getAllMethods(getVistingClass())) {
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
	@SuppressWarnings("unchecked")
	private class Visitor {

		/**
		 * @param annotation
		 * @param method
		 */
		public void visitAnnotationMethod(Annotation annotation, Method method) {
			H handler = (H) AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
				handler.handle(metaData, annotation, method);
			}
		}
		
		/**
		 * @param annotation
		 * @param field
		 */
		public void visitAnnotationField(Annotation annotation, Field field) {
			H handler = (H) AbstractAnnotationHandler.handlerFor(annotation);
			if (handler != null) {
				handler.handle(metaData, annotation, field);
			}
		}
	}
}
