/**
 * 
 */
package org.minnal.instrument.resource.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class ResourceMetaDataProvider {

	private Map<Class<?>, ResourceMetaData> metaDataMap = new HashMap<Class<?>, ResourceMetaData>();
	
	private static ResourceMetaDataProvider provider = new ResourceMetaDataProvider();
	
	private ResourceMetaDataProvider() {
	}
	
	public static ResourceMetaDataProvider instance() {
		return provider;
	}
	
	public ResourceMetaData getResourceMetaData(Class<?> resourceClass) {
		ResourceMetaData metaData = metaDataMap.get(resourceClass);
		if (metaData == null) {
			metaData = getResourceMetaDataBuilder(resourceClass).build();
			metaDataMap.put(resourceClass, metaData);
		}
		return metaData;
	}
	
	protected ResourceMetaDataBuilder getResourceMetaDataBuilder(Class<?> resourceClass) {
		return new ResourceMetaDataBuilder(resourceClass);
	}
}
