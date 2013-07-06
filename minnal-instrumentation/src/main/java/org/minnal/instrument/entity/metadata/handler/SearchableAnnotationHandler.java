/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.Searchable;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ParameterMetaData;

import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class SearchableAnnotationHandler extends AbstractAnnotationHandler {

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		String value = ((Searchable)annotation).value();
		value = Strings.isNullOrEmpty(value) ? getGetterName(method, true) : value;
		ParameterMetaData parameterMetaData = new ParameterMetaData(value, value, method.getReturnType());
		metaData.addSearchField(parameterMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		String value = ((Searchable)annotation).value();
		if (Strings.isNullOrEmpty(value)) {
			value = field.getName();
		}
		ParameterMetaData parameterMetaData = new ParameterMetaData(value, field.getName(), field.getType());
		metaData.addSearchField(parameterMetaData);
	}

	@Override
	public Class<?> getAnnotationType() {
		return Searchable.class;
	}

}
