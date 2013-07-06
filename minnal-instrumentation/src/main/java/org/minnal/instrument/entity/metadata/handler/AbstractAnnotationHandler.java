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
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

import javax.persistence.Entity;

import org.minnal.instrument.entity.metadata.EntityMetaData;

import com.google.common.base.CaseFormat;

/**
 * @author ganeshs
 *
 */
public abstract class AbstractAnnotationHandler {
	
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
	
	public abstract void handle(EntityMetaData metaData, Annotation annotation, Method method);
	
	public abstract void handle(EntityMetaData metaData, Annotation annotation, Field field);
	
	/**
	 * Returns the name of the getter method. The checkGetter throws an exception if the method is not a getter
	 * 
	 * @param method
	 * @param checkGetter
	 * @return
	 */
	protected String getGetterName(Method method, boolean checkGetter) {
		String name = method.getName();
		if (name.startsWith("get")) {
			return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name.substring(3));
		}
		if (! checkGetter) {
			return name;
		}
		throw new IllegalArgumentException("Method - " + method.getName() + " is not a getter");
	}
	
	/**
	 * Returns the element type of the collection. If the collection is a map, returns the value type. If its not a collection returns Object
	 * 
	 * @param type
	 * @return
	 */
	protected Class<?> getElementType(Type type) {
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
	
	protected boolean isEntity(Class<?> entityClass) {
		return entityClass.isAnnotationPresent(Entity.class);
	}

}
