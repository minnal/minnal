/**
 * 
 */
package org.minnal.core.resource;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.minnal.utils.reflection.ClassUtils;
import org.minnal.utils.reflection.PropertyUtil;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import uk.co.jemos.podam.api.PodamFactoryListener;

/**
 * @author ganeshs
 *
 */
public class PodamFactoryListenerImpl implements PodamFactoryListener {

	@Override
	public void onAttributeSet(Object pojo, Method setter, Object value) {
		PropertyDescriptor descriptor = ClassUtils.getPropertyDescriptorFromMethod(setter);
		if (PropertyUtil.isCollectionProperty(descriptor.getPropertyType(), false)) {
			handleCollectionProperty(pojo, descriptor, value);
		}
	}

	protected void handleCollectionProperty(Object pojo, PropertyDescriptor descriptor, Object value) {
		if (value == null) {
			return;
		}
		JsonManagedReference managedReference = PropertyUtil.getAnnotation(descriptor, JsonManagedReference.class);
		if (managedReference != null) {
			PropertyDescriptor backReference = getManagedBackReference(PropertyUtil.getType(descriptor), managedReference.value());
			if (backReference != null) {
				for (Object element : (Collection<?>) value) {
					try {
						PropertyUtils.setProperty(element, backReference.getName(), pojo);
					} catch (Exception e) {
						e.printStackTrace();
						// TODO log exception and ignore
					}
				}
			}
		}
	}
	
	private PropertyDescriptor getManagedBackReference(Class<?> clazz, String name) {
		List<PropertyDescriptor> descriptors = PropertyUtil.getPopertiesWithAnnotation(clazz, JsonBackReference.class);
		if (descriptors == null) {
			return null;
		}
		for (PropertyDescriptor descriptor : descriptors) {
			JsonBackReference backReference = PropertyUtil.getAnnotation(descriptor, JsonBackReference.class);
			if (backReference.value().equals(name)) {
				return descriptor;
			}
		}
		return null;
	}
	
}
