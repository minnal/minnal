package org.minnal.jaxrs.test;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.apache.commons.beanutils.PropertyUtils;
import org.minnal.autopojo.AttributeMetaData;
import org.minnal.autopojo.resolver.CollectionResolver;
import org.minnal.autopojo.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.util.List;

public class BiDirectionalObjectResolver extends CollectionResolver {

    private static final Logger logger = LoggerFactory.getLogger(BiDirectionalObjectResolver.class);

    @Override
    protected void setAttribute(Object pojo, AttributeMetaData attribute, Object value) {
        super.setAttribute(pojo, attribute, value);
        JsonManagedReference managedReference = attribute.getAnnotation(JsonManagedReference.class);
        if (managedReference != null && value != null) {
            PropertyDescriptor backReference = getManagedBackReference(value.getClass(), managedReference.value());
            if (backReference != null) {
                try {
                    PropertyUtils.setProperty(value, backReference.getName(), pojo);
                } catch (Exception e) {
                    logger.info("Failed while setting the property {} on the class {}", backReference.getName(), value.getClass());
                }
            }
        }
    }

    private PropertyDescriptor getManagedBackReference(Class<?> clazz, String name) {
        List<PropertyDescriptor> descriptors = PropertyUtil.getPopertiesWithAnnotation(clazz, JsonBackReference.class);
        if (descriptors != null) {
            for (PropertyDescriptor descriptor : descriptors) {
                JsonBackReference backReference = PropertyUtil.getAnnotation(descriptor, JsonBackReference.class);
                if (backReference.value().equals(name)) {
                    return descriptor;
                }
            }
        }
        return null;
    }


}