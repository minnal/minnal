/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.ws.rs.Path;

import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.instrument.resource.metadata.ResourceMethodMetaData;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.reflection.ClassUtils;

/**
 * @author ganeshs
 *
 */
public abstract class HttpMethodAnnotationHandler extends AbstractResourceAnnotationHandler {

	@Override
	public void handle(ResourceMetaData metaData, Annotation annotation, Method method) {
		Path path = ClassUtils.getAnnotation(method, Path.class);
		String methodPath = HttpUtil.concatPaths(metaData.getPath(), path != null ? path.value() : null);
		ResourceMethodMetaData resourceMethod = new ResourceMethodMetaData(methodPath, getHttpMethod(), method);
		metaData.addResourceMethod(resourceMethod);
	}

	@Override
	public void handle(ResourceMetaData metaData, Annotation annotation, Field field) {
		// Do nothing
	}
	
	/**
	 * Returns the http method
	 * 
	 * @return
	 */
	protected abstract String getHttpMethod();

}
