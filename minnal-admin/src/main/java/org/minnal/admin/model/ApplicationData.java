/**
 * 
 */
package org.minnal.admin.model;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.core.db.DataSourceStatistics;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class ApplicationData {
	
	private Application<ApplicationConfiguration> application;
	
	/**
	 * @param application
	 */
	public ApplicationData(Application<ApplicationConfiguration> application) {
		this.application = application;
	}

	/**
	 * @return the name
	 */
	@JsonProperty
	public String getName() {
		return application.getConfiguration().getName();
	}

	/**
	 * @return the dataSourceStatistics
	 */
	@JsonProperty
	public DataSourceStatistics getDataSourceStatistics() {
		DatabaseConfiguration dbConfiguration = application.getConfiguration().getDatabaseConfiguration();
		if (dbConfiguration != null) {
			return dbConfiguration.getDataSourceProvider().getStatistics();					
		}
		return null;
	}
}
