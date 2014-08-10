/**
 * 
 */
package org.minnal.instrument.resource.metadata.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.minnal.instrument.resource.metadata.ResourceMetaData;
import org.minnal.utils.http.HttpUtil;
import org.minnal.utils.reflection.ClassUtils;

import com.google.common.collect.Sets;

/**
 * @author ganeshs
 *
 */
public class PathAnnotationHandler extends AbstractResourceAnnotationHandler {
	
	public static final Set<Class<? extends Annotation>> httpMethods = Sets.newHashSet(GET.class, PUT.class, DELETE.class, POST.class, HEAD.class, OPTIONS.class);

	/**
	 * @param metaData
	 * @param annotation
	 * @param method
	 */
	public void handle(ResourceMetaData metaData, Annotation annotation, Method method) {
		if (! hasHttpMethod(method)) {
			ResourceMetaData subResource = new ResourceMetaData(method.getReturnType(), HttpUtil.concatPaths(metaData.getPath(), ((Path)annotation).value()));
			metaData.addSubResource(subResource);
		}
	}

	public Class<?> getAnnotationType() {
		return Path.class;
	}
	
	/**
	 * Checks if a HTTP method annotation is defined for the given method
	 * 
	 * @param method
	 * @return
	 */
	protected boolean hasHttpMethod(Method method) {
		for (Class<? extends Annotation> annotationClass : httpMethods) {
			if (ClassUtils.hasAnnotation(method, annotationClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void handle(ResourceMetaData metaData, Annotation annotation, Field field) {
		// do nothing
	}
}
