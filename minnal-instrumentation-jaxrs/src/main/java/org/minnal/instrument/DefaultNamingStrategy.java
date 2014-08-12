/**
 * 
 */
package org.minnal.instrument;

import org.javalite.common.Inflector;
import org.minnal.instrument.entity.metadata.CollectionMetaData;

/**
 * @author ganeshs
 *
 */
public class DefaultNamingStrategy implements NamingStrategy {

	@Override
	public String getEntityName(Class<?> entityClass) {
		return Inflector.camelize(Inflector.underscore(entityClass.getSimpleName()), false);
	}

	@Override
	public String getEntityName(CollectionMetaData collection) {
		return Inflector.singularize(collection.getName());
	}
	
	@Override
	public String getResourceName(String entityName) {
		return Inflector.tableize(entityName);
	}

	@Override
	public String getEntityCollectionName(String name) {
		return Inflector.singularize(name);
	}

	@Override
	public String getResourceClassName(Class<?> entityClass) {
		return entityClass.getName() + "Resource";
	}

	@Override
	public String getResourceWrapperClassName(Class<?> resourceClass) {
		return resourceClass.getName() + "Wrapper";
	}

	@Override
	public String getPathSegment(String segment) {
		return Inflector.underscore(segment);
	}

	@Override
	public String getResourceName(Class<?> entityClass) {
		return getResourceName(getEntityName(entityClass));
	}

	@Override
	public String getQueryParamName(String name) {
		return Inflector.underscore(name);
	}
}
