/**
 * 
 */
package org.minnal.instrument;

import org.minnal.instrument.entity.metadata.CollectionMetaData;

/**
 * @author ganeshs
 *
 */
public interface NamingStrategy {

	/**
	 * @param entityClass
	 * @return
	 */
	String getEntityName(Class<?> entityClass);
	
	/**
	 * @param collection
	 * @return
	 */
	String getEntityName(CollectionMetaData collection);
	
	/**
	 * @param entityName
	 * @return
	 */
	String getResourceName(String entityName);
	
	/**
	 * Return back the sub resource name given a collection field name
	 * 
	 * @param collectionName
	 * @return
	 */
	String getSubResourceName(String collectionName);
	
}
