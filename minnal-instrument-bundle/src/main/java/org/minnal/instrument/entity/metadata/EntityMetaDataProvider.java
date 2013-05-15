/**
 * 
 */
package org.minnal.instrument.entity.metadata;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ganeshs
 *
 */
public class EntityMetaDataProvider {

	private Map<Class<?>, EntityMetaData> metaDataMap = new HashMap<Class<?>, EntityMetaData>();
	
	private static EntityMetaDataProvider provider = new EntityMetaDataProvider();
	
	private EntityMetaDataProvider() {
	}
	
	public static EntityMetaDataProvider instance() {
		return provider;
	}
	
	public EntityMetaData getEntityMetaData(Class<?> entityClass) {
		EntityMetaData metaData = metaDataMap.get(entityClass);
		if (metaData == null) {
			metaData = getEntityMetaDataBuilder(entityClass).build();
			metaDataMap.put(entityClass, metaData);
		}
		return metaData;
	}
	
	protected EntityMetaDataBuilder getEntityMetaDataBuilder(Class<?> clazz) {
		return new EntityMetaDataBuilder(clazz);
	}
}
