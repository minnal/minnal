/**
 * 
 */
package org.minnal.instrument.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author anand.karthik
 *
 */
public class ClassUtils {

	/**
	 * Gets all the declared fields in the Class and all the Inherited Classes
	 * 
	 * @author anand.karthik
	 */
	public static List<Field> getAllFields(Class<?> type) {
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(type.getDeclaredFields()));
		if (type.getSuperclass() != null) {
	        fields.addAll(getAllFields(type.getSuperclass()));
	    }
		return fields;
	}
	
	/**
	 * Gets all the declared mehtods in the Class and all the Inherited Classes
	 * 
	 * @author anand.karthik
	 */
	public static List<Method> getAllMethods(Class<?> type) {
		List<Method> methods = new ArrayList<Method>();
		methods.addAll(Arrays.asList(type.getDeclaredMethods()));
		if (type.getSuperclass() != null) {
	        methods.addAll(getAllMethods(type.getSuperclass()));
	    }
	    return methods;
	}

	public static boolean hasAnnotation(Field field, Class<? extends Annotation> clazz) {
		return field.isAnnotationPresent(clazz);
	}
	
	public static boolean hasAnnotation(Method method, Class<? extends Annotation> clazz) {
		return method.isAnnotationPresent(clazz);
	}
}
