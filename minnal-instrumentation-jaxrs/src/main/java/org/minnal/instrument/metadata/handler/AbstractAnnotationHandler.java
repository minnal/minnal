/**
 * 
 */
package org.minnal.instrument.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import org.minnal.instrument.metadata.MetaData;

/**
 * @author ganeshs
 *
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractAnnotationHandler<T extends MetaData> {
	
	private static Map<Class<?>, AbstractAnnotationHandler> handlers;
	
	public static AbstractAnnotationHandler handlerFor(Annotation annotation) {
		if (handlers == null) {
			 handlers = new HashMap<Class<?>, AbstractAnnotationHandler>();
			 ServiceLoader<AbstractAnnotationHandler> loader = ServiceLoader.load(AbstractAnnotationHandler.class);
			 for (AbstractAnnotationHandler handler : loader) {
				 handlers.put(handler.getAnnotationType(), handler);
			 }
		}
		return handlers.get(annotation.annotationType());
	}
	
	public abstract Class<?> getAnnotationType();
	
	public abstract void handle(T metaData, Annotation annotation, Method method);
	
	public abstract void handle(T metaData, Annotation annotation, Field field);
	
}
