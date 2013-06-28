package org.minnal.instrument.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedList;

public class ClassUtils {

	public ClassUtils() {
		// TODO Auto-generated constructor stub
	}
	
	public static LinkedList<Field> getAllFields(LinkedList<Field> fields, Class<?> type) {
	    for (Field field: type.getDeclaredFields()) {
	        fields.add(field);
	    }

	    if (type.getSuperclass() != null) {
	        fields = getAllFields(fields, type.getSuperclass());
	    }

	    return fields;
	}
	
	public static LinkedList<Method> getAllMethods(LinkedList<Method> methods, Class<?> type) {
	    for (Method method: type.getDeclaredMethods()) {
	        methods.add(method);
	    }

	    if (type.getSuperclass() != null) {
	        methods = getAllMethods(methods, type.getSuperclass());
	    }

	    return methods;
	}

}
