/**
 * 
 */
package org.minnal.api.filter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.minnal.utils.reflection.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.Option;
import scala.collection.immutable.Map;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.wordnik.swagger.converter.SwaggerSchemaConverter;
import com.wordnik.swagger.model.Model;

/**
 * @author ganeshs
 * 
 */
public class JacksonModelConvertor extends SwaggerSchemaConverter {
	
	private List<Class<? extends Annotation>> excludeAnnotations;
	
	private String baseModelClassName = "org.activejpa.entity.Model";
	
	private Class<?> baseModelClass;
	
	private static final Logger logger = LoggerFactory.getLogger(JacksonModelConvertor.class);
	
	/**
	 * Default constructor
	 */
	public JacksonModelConvertor() {
		try {
			baseModelClass = Class.forName(baseModelClassName);
		} catch (Exception e) {
			logger.error("Failed while loading the model class", e); 
		}
	}

	@Override
	public Option<Model> read(Class<?> cls, Map<String, String> typeMap) {
		Option<Model> ret = super.read(cls, typeMap);
		if (baseModelClass == null) {
			return ret;
		}
		if (baseModelClass.isAssignableFrom(cls)) {
			Class<?> clazz = cls;
			while (clazz.getSuperclass() != null) {
				for (Field f : cls.getDeclaredFields()) {
					if (ClassUtils.hasAnnotation(clazz, f.getName(), JsonBackReference.class)) {
						ret.get().properties().remove(f.getName());
					}
				}
				clazz = clazz.getSuperclass();
			}
		}
		return ret;
	}
}
