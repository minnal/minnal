/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import javax.ws.rs.Path;

import org.minnal.instrument.metadata.MetaDataBuilder;
import org.minnal.instrument.resource.metadata.handler.AbstractResourceAnnotationHandler;
import org.minnal.utils.reflection.ClassUtils;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaDataBuilder extends MetaDataBuilder<ResourceMetaData, AbstractResourceAnnotationHandler>{
	
	/**
	 * @param resourceClass
	 */
	public ResourceMetaDataBuilder(Class<?> resourceClass) {
		super(new ResourceMetaData(resourceClass, ClassUtils.getAnnotation(resourceClass, Path.class).value()));
	}
	
	/**
	 * @param metaData
	 */
	ResourceMetaDataBuilder(ResourceMetaData metaData) {
		super(metaData);
	}
	
	@Override
	public ResourceMetaData build() {
		ResourceMetaData metaData = super.build();
		for (ResourceMetaData subResource : metaData.getSubResources()) {
			ResourceMetaDataBuilder builder = new ResourceMetaDataBuilder(subResource);
			builder.build();
		}
		return metaData;
	}

	@Override
	protected Class<?> getVistingClass() {
		return getMetaData().getResourceClass();
	}
}
