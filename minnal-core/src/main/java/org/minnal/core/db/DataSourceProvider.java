/**
 * 
 */
package org.minnal.core.db;

import javax.sql.DataSource;

import org.minnal.core.config.DatabaseConfiguration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * @author ganeshs
 *
 */
@JsonTypeInfo(use=Id.CLASS, include=As.PROPERTY, property="class")
public interface DataSourceProvider {

	DataSource getDataSource();
	
	void close();
	
	DataSourceStatistics getStatistics();
	
	void setConfiguration(DatabaseConfiguration configuration);
}
