/**
 * 
 */
package org.minnal.utils.reflection;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.google.common.base.CaseFormat;

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
	
	public static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static boolean hasAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		return clazz.isAnnotationPresent(annotationClass);
	}

	public static boolean hasAnnotation(Field field, Class<? extends Annotation> clazz) {
		return field.isAnnotationPresent(clazz);
	}
	
	public static boolean hasAnnotation(Method method, Class<? extends Annotation> clazz) {
		return method.isAnnotationPresent(clazz);
	}
	
	public static PropertyDescriptor getPropertyDescriptorFromMethod(Method method) {
		String name = method.getName();
		if (name.startsWith("get") || name.startsWith("set")) {
			try {
				return getPropertyDescriptor(method.getDeclaringClass(), CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name.substring(3)));
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}
		return null;
	}
	
	public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String name) {
		for (PropertyDescriptor descriptor : PropertyUtils.getPropertyDescriptors(clazz)) {
			if (descriptor.getName().equals(name)) {
				return descriptor;
			}
		}
		return null;
	}
	
	public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationClass) {
		return clazz.getAnnotation(annotationClass);
	}
	
	public static <T extends Annotation> T getAnnotation(Method method, Class<T> clazz) {
		return method.getAnnotation(clazz);
	}
	
	public static <T extends Annotation> T getAnnotation(Class<?> clazz, String property, Class<T> annotationClass) {
		PropertyDescriptor descriptor = getPropertyDescriptor(clazz, property);
		if (descriptor == null) {
			Field field = getField(clazz, property);
			if (field != null) {
				return field.getAnnotation(annotationClass);
			}
			return null;
		}
		return PropertyUtil.getAnnotation(descriptor, annotationClass);
	}
}
