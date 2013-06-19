/**
 * 
 */
package org.minnal.core.db;

import javax.sql.DataSource;

import org.minnal.core.config.DatabaseConfiguration;

/**
 * @author ganeshs
 *
 */
public interface DataSourceProvider {

	DataSource getDataSource();
	
	void close();
	
	DataSourceStatistics getStatistics();
	
	void setConfiguration(DatabaseConfiguration configuration);
}
