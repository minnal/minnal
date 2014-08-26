/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.minnal.instrument.MinnalInstrumentationException;
import org.minnal.instrument.entity.Action;
import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.metadata.ActionMetaData;
import org.minnal.instrument.entity.metadata.EntityMetaData;
import org.minnal.instrument.entity.metadata.ParameterMetaData;
import org.minnal.instrument.util.ParameterNameDiscoverer;
import org.minnal.utils.reflection.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

/**
 * @author ganeshs
 *
 */
public class ActionAnnotationHandler extends AbstractEntityAnnotationHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(ActionAnnotationHandler.class);
	
	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Method method) {
		if (! ClassUtils.hasAnnotation(metaData.getEntityClass(), AggregateRoot.class)) {
			logger.error("@Action can be specified only on classes marked with @AggregateRoot. {} class is not an aggregate root", metaData.getEntityClass());
			throw new MinnalInstrumentationException("@Action can be specified only on classes marked with @AggregateRoot. " + 
					metaData.getEntityClass() + " class is not an aggregate root");
		}
		String value = ((Action)annotation).value();
		String path = ((Action)annotation).path();
		if (Strings.isNullOrEmpty(value)) {
			value = method.getName();
		}
		if (Strings.isNullOrEmpty(path)) {
			path = null;
		}
		ActionMetaData actionMetaData = new ActionMetaData(value, path, method);
		String[] parameterNames = ParameterNameDiscoverer.getParameterNames(method);
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			ParameterMetaData parameterMetaData = new ParameterMetaData(parameterNames[i], parameterNames[i], parameterTypes[i]);
			actionMetaData.addParameter(parameterMetaData);
		}
		metaData.addActionMethod(actionMetaData);
	}

	@Override
	public void handle(EntityMetaData metaData, Annotation annotation, Field field) {
		throw new IllegalArgumentException("@Action should be annotated only over methods");
	}

	@Override
	public Class<?> getAnnotationType() {
		return Action.class;
	}

}
