/**
 * 
 */
package org.minnal.api.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;

/**
 * @author ganeshs
 *
 */
public class PropertyUtil {
	
	/**
	 * Check if the given type represents a "simple" property:
	 * a primitive, a String or other CharSequence, a Number, a Date,
	 * a URI, a URL, a Locale, a Class, or a corresponding array.
	 * <p>Used to determine properties to check for a "simple" dependency-check.
	 * @param clazz the type to check
	 * @return whether the given type represents a "simple" property
	 * @see org.springframework.beans.factory.support.RootBeanDefinition#DEPENDENCY_CHECK_SIMPLE
	 * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#checkDependencies
	 */
	public static boolean isSimpleProperty(Class<?> clazz) {
		return isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType()));
	}

	/**
	 * Check if the given type represents a "simple" value type:
	 * a primitive, a String or other CharSequence, a Number, a Date,
	 * a URI, a URL, a Locale or a Class.
	 * 
	 * @param clazz the type to check
	 * @return whether the given type represents a "simple" value type
	 */
	public static boolean isSimpleValueType(Class<?> clazz) {
		return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				clazz.equals(URI.class) || clazz.equals(URL.class) ||
				clazz.equals(Locale.class) || clazz.equals(Class.class) || 
				clazz.equals(Serializable.class) || clazz.equals(Timestamp.class);
	}
	
	public static boolean isCollectionProperty(Type type, boolean includeMaps) {
		if (type instanceof Class) {
			return isCollectionProperty((Class<?>)type, includeMaps);
		}
		if (type instanceof ParameterizedType) {
			return isCollectionProperty((Class<?>)((ParameterizedType) type).getRawType(), includeMaps);
		}
		return false;
	}
	
	public static boolean isCollectionProperty(Class<?> clazz, boolean includeMaps) {
		return Collection.class.isAssignableFrom(clazz) || (includeMaps && isMapProperty(clazz));
	}
	
	public static boolean isMapProperty(Type type) {
		if (type instanceof Class) {
			return isMapProperty((Class<?>)type);
		}
		if (type instanceof ParameterizedType) {
			return isMapProperty((Class<?>)((ParameterizedType) type).getRawType());
		}
		return false;
	}
	
	public static boolean isMapProperty(Class<?> clazz) {
		return Map.class.isAssignableFrom(clazz);
	}
	
	public static Class<?> getCollectionElementType(Type type) {
		ParameterizedType ptype = (ParameterizedType) type;
		Class<?> rawType = (Class<?>) ptype.getRawType();
		
		try {
			if (Collection.class.isAssignableFrom(rawType)) {
				return TypeToken.of(type).resolveType(Collection.class.getMethod("add", Object.class).getGenericParameterTypes()[0]).getRawType();
			}
			if (Map.class.isAssignableFrom(rawType)) {
				return TypeToken.of(type).resolveType(Map.class.getMethod("put", Object.class, Object.class).getGenericParameterTypes()[1]).getRawType();
			}
		} catch (Exception e) {
			// TODO logo exception and ignore
		}
		return Object.class;
	}
	
	private static <E> TypeToken<Iterator<E>> iteratorOf(TypeToken<E> elementType) {
		return new TypeToken<Iterator<E>>() {}.where(new TypeParameter<E>() {}, elementType);
	}
	
	public static boolean canSerialize(PropertyDescriptor descriptor) {
		Method method = descriptor.getReadMethod();
		if (method == null) {
			method = descriptor.getWriteMethod();
		}
		if (method == null || method.getDeclaringClass().equals(Object.class)) {
			return false;
		}
		return ! hasAnnotation(descriptor, JsonIgnore.class);
	}
	
	public static boolean hasAnnotation(PropertyDescriptor descriptor, Class<? extends Annotation> clazz) {
		Method method = descriptor.getReadMethod();
		if (method == null) {
			return false;
		}
		
		Field field = FieldUtils.getField(method.getDeclaringClass(), descriptor.getName(), true);
		if (field == null) {
			return false;
		}
		if (field.isAnnotationPresent(clazz)) {
			return true;
		}
		
		return hasAnnotation(method, clazz);
	}
	
	public static boolean hasAnnotation(Field field, Class<? extends Annotation> clazz) {
		return field.isAnnotationPresent(clazz);
	}
	
	public static boolean hasAnnotation(Method method, Class<? extends Annotation> clazz) {
		return method.isAnnotationPresent(clazz);
	}
	
	public static List<String> getEnumValues(PropertyDescriptor descriptor) {
		List<String> values = new ArrayList<String>();
		try {
			Enum[] enums = (Enum[])descriptor.getPropertyType().getMethod("values").invoke(null);
			for (Enum enm : enums) {
				values.add(enm.name());
			}
		} catch (Exception e) {
			// TODO Log exception and ignore
		}
		return values;
	}
	
	public static Class<?> getType(PropertyDescriptor descriptor) {
		Method method = descriptor.getReadMethod();
		if (method == null) {
			method = descriptor.getWriteMethod();
		}
		if (method == null) {
			return null;
		}
		Type type = method.getGenericReturnType();
		if (type instanceof ParameterizedType) {
			if (isCollectionProperty(type, true)) {
				return getCollectionElementType(type);
			}
			return (Class<?>)((ParameterizedType)type).getRawType(); 
		}
		return (Class<?>) type;
	}
}
