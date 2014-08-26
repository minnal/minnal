/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.minnal.instrument.entity.AggregateRoot;
import org.minnal.instrument.entity.Secure;
import org.minnal.instrument.entity.SecureMultiple;
import org.minnal.instrument.entity.metadata.handler.SecureAnnotationHandler;
import org.minnal.instrument.entity.metadata.handler.SecureMultipleAnnotationHandler;
import org.minnal.instrument.metadata.MetaData;
import org.minnal.utils.reflection.ClassUtils;

/**
 * @author ganeshs
 *
 */
public class SecurableMetaData extends MetaData {

	private Set<PermissionMetaData> permissionMetaData = new HashSet<PermissionMetaData>();
	
	/**
	 * @param name
	 */
	public SecurableMetaData(String name) {
		super(name);
	}
	
	protected void init(AnnotatedElement element) {
		Secure secure = null;
		SecureMultiple secureMultiple = null;
		if (element instanceof Class && ClassUtils.hasAnnotation((Class<?>) element, AggregateRoot.class)) {
			secureMultiple = ClassUtils.getAnnotation((Class<?>) element, SecureMultiple.class);
			secure = ClassUtils.getAnnotation((Class<?>) element, Secure.class);
		} else if (element instanceof Field) {
			Field member = (Field) element;
			secureMultiple = member.getAnnotation(SecureMultiple.class);
			secure = member.getAnnotation(Secure.class);
		} else if (element instanceof Method) {
			Method member = (Method) element;
			secureMultiple = member.getAnnotation(SecureMultiple.class);
			secure = member.getAnnotation(Secure.class);
		}
		
		if (secure != null) {
			new SecureAnnotationHandler().handle(this, secure);
		}
		if (secureMultiple != null) {
			new SecureMultipleAnnotationHandler().handle(this, secureMultiple);
		}
	}
	
	/**
	 * @return the permissionMetaData
	 */
	public Set<PermissionMetaData> getPermissionMetaData() {
		return permissionMetaData;
	}
	
	/**
	 * @param data
	 */
	public void addPermissionMetaData(PermissionMetaData data) {
		permissionMetaData.add(data);
	}
}
