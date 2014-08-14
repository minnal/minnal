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

import com.google.common.collect.Lists;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;
import com.wordnik.swagger.model.ModelProperty;

/**
 * @author ganeshs
 * 
 */
public class JacksonModelConvertor extends SwaggerSchemaConverter {
	
	private List<Class<? extends Annotation>> excludeAnnotations = Lists.newArrayList();
	
	/**
	 * @param excludeAnnotations
	 */
	public JacksonModelConvertor(List<Class<? extends Annotation>> excludeAnnotations) {
		this.excludeAnnotations = excludeAnnotations;
	}

	@Override
	public Option<Model> read(Class<?> cls, Map<String, String> typeMap) {
		Option<Model> model = super.read(cls, typeMap);
		Class<?> clazz = cls;
		while (clazz.getSuperclass() != null) {
			for (Field field : cls.getDeclaredFields()) {
				handleExcludedAnnotations(clazz, field, model);
				handleTimestampField(field, model);
			}
			clazz = clazz.getSuperclass();
		}
		return model;
	}
	
	/**
	 * Handles excluded annotations
	 * 
	 * @param clazz
	 * @param field
	 * @param model
	 */
	protected void handleExcludedAnnotations(Class<?> clazz, Field field, Option<Model> model) {
		for (Class<? extends Annotation> annotationClass : excludeAnnotations) {
			if (ClassUtils.hasAnnotation(clazz, field.getName(), annotationClass)) {
				model.get().properties().remove(field.getName());
			}
		}
	}
	
	/**
	 * Handles the timestamp field
	 * 
	 * @param field
	 * @param model
	 */
	protected void handleTimestampField(Field field, Option<Model> model) {
		if (Timestamp.class.isAssignableFrom(field.getType())) {
			ModelProperty property = model.get().properties().get(field.getName()).get();
			ModelProperty newProperty = new ModelProperty("date-time", property.qualifiedType(), property.position(), property.required(),
					property.description(), property.allowableValues(), property.items());
			model.get().properties().put(field.getName(), newProperty);
		}
	}

	/**
	 * @return the excludeAnnotations
	 */
	public List<Class<? extends Annotation>> getExcludeAnnotations() {
		return excludeAnnotations;
	}
}
