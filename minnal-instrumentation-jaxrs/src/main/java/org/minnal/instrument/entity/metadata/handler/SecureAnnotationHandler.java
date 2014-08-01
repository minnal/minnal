/**
 * 
 */
package org.minnal.instrument.entity.metadata.handler;

import java.lang.annotation.Annotation;

import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.Secure.Method;
import org.minnal.instrument.entity.metadata.PermissionMetaData;
import org.minnal.instrument.entity.metadata.SecurableMetaData;

import com.google.common.collect.Lists;

/**
 * @author ganeshs
 *
 */
public class SecureAnnotationHandler {

	public void handle(SecurableMetaData metaData, Annotation annotation) {
		metaData.addPermissionMetaData(constructPermissionMetaData(annotation));
	}

	protected PermissionMetaData constructPermissionMetaData(Annotation annotation) {
		Method method = ((Secure) annotation).method();
		String[] permissions = ((Secure) annotation).permissions();
		return new PermissionMetaData(method.getMethod(), Lists.newArrayList(permissions));
	}
}
