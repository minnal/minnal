/**
 * 
 */
package org.minnal.api.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.minnal.utils.reflection.ClassUtils;

import scala.Option;
import scala.collection.immutable.Map;

import com.google.common.collect.Lists;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;

/**
 * @author ganeshs
 * 
 */
public class ExcludeAnnotationsConvertor extends SwaggerSchemaConverter {
	
	private List<Class<? extends Annotation>> excludeAnnotations = Lists.newArrayList();
	
	/**
	 * @param excludeAnnotations
	 */
	public ExcludeAnnotationsConvertor(List<Class<? extends Annotation>> excludeAnnotations) {
		this.excludeAnnotations = excludeAnnotations;
	}

	@Override
	public Option<Model> read(Class<?> cls, Map<String, String> typeMap) {
		Option<Model> model = super.read(cls, typeMap);
		for (Field field : cls.getDeclaredFields()) {
			handleExcludedAnnotations(cls, field, model);
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
	 * @return the excludeAnnotations
	 */
	public List<Class<? extends Annotation>> getExcludeAnnotations() {
		return excludeAnnotations;
	}
}
