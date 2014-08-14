/**
 * 
 */
package org.minnal.api.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.List;

import org.minnal.utils.reflection.ClassUtils;

import scala.Option;
import scala.collection.immutable.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;

/**
 * @author ganeshs
 * 
 */
public class JacksonModelConvertor extends SwaggerSchemaConverter {
	
	private List<Class<? extends Annotation>> excludeAnnotations;
	
	@Override
	public Option<Model> read(Class<?> cls, Map<String, String> typeMap) {
		Option<Model> ret = super.read(cls, typeMap);
		Class<?> clazz = cls;
		while (clazz.getSuperclass() != null) {
			for (Field f : cls.getDeclaredFields()) {
				if (ClassUtils.hasAnnotation(clazz, f.getName(), JsonBackReference.class)) {
					ret.get().properties().remove(f.getName());
				}
				if (Timestamp.class.isAssignableFrom(f.getType())) {
					ModelProperty property = ret.get().properties().get(f.getName()).get();
					ModelProperty newProperty = new ModelProperty("date-time", property.qualifiedType(), property.position(), property.required(),
							property.description(), property.allowableValues(), property.items());
					ret.get().properties().put(f.getName(), newProperty);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return ret;
	}
}
