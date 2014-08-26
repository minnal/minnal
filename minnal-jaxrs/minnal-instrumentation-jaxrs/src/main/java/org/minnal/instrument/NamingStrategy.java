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
	 * @param entityName
	 * @return
	 */
	String getResourceName(Class<?> entityClass);
	
	/**
	 * @param entityName
	 * @return
	 */
	String getPathSegment(String segment);
	
	/**
	 * Return back the entity resource name given a collection field name
	 * 
	 * @param name
	 * @return
	 */
	String getEntityCollectionName(String name);
	
	/**
	 * Returns the resource class name from the entity class
	 * 
	 * @param entityClass
	 * @return
	 */
	String getResourceClassName(Class<?> entityClass);
	
	/**
	 * Returns the resource wrapper class name from the resource class
	 * 
	 * @param resourceClass
	 * @return
	 */
	String getResourceWrapperClassName(Class<?> resourceClass);
	
	/**
	 * Returns the query param name
	 * 
	 * @param name
	 * @return
	 */
	String getQueryParamName(String name);
	
}
