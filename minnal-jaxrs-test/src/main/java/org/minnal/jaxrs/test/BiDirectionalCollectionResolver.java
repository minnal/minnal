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
import java.util.Collection;
import java.util.List;

/**
 * @author ganeshs
 */
public class BiDirectionalCollectionResolver extends CollectionResolver {

    private static final Logger logger = LoggerFactory.getLogger(BiDirectionalCollectionResolver.class);

    @Override
    public void resolve(Object pojo, AttributeMetaData attribute, int maxDepth) {
        // Prevent looping incase of composite models
        if (maxDepth > 2 && attribute.getTypeArguments()[0].equals(pojo.getClass())) {
            return;
        }
        super.resolve(pojo, attribute, maxDepth);
    }

    @Override
    protected void setAttribute(Object pojo, AttributeMetaData attribute, Object value) {
        super.setAttribute(pojo, attribute, value);
        JsonManagedReference managedReference = attribute.getAnnotation(JsonManagedReference.class);
        if (managedReference != null && value != null) {
            Class<?> elementType = (Class<?>) attribute.getTypeArguments()[0];
            PropertyDescriptor backReference = getManagedBackReference(elementType, managedReference.value());
            if (backReference != null) {
                Collection collection = (Collection) value;
                for (Object object : collection) {
                    try {
                        PropertyUtils.setProperty(object, backReference.getName(), pojo);
                    } catch (Exception e) {
                        logger.info("Failed while setting the property {} on the class {}", backReference.getName(), value.getClass());
                    }
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
