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
	public String getSubResourceName(String collectionName) {
		return Inflector.underscore(collectionName);
	}
}
