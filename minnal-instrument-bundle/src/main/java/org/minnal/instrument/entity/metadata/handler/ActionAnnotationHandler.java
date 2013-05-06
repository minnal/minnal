/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.minnal.instrument.util.ParameterNameDiscoverer;

/**
 * @author ganeshs
 *
 */
public class ActionAnnotationHandler extends AbstractAnnotationHandler {
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		String value = ((Action)annotation).value();
		if (value.isEmpty()) {
			value = method.getName();
		}
		ActionMetaData actionMetaData = new ActionMetaData(value, method);
		String[] parameterNames = ParameterNameDiscoverer.getParameterNames(method);
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ParameterMetaData parameterMetaData = new ParameterMetaData(parameterNames[i], parameterNames[i], parameterTypes[i]);
			actionMetaData.addParameter(parameterMetaData);
		}
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		throw new IllegalStateException("@Action should be annotated only over methods");
	}

	@Override
	public Class<?> getAnnotationType() {
		return Action.class;
	}

}
