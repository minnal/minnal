/**
 * 
 */
package org.minnal.core.config;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author ganeshs
 *
 */
public class ApplicationConfiguration extends Configuration {
	
	private List<String> packagesToScan = new ArrayList<String>();
	
	private DatabaseConfiguration databaseConfiguration;
	
	private boolean instrumentationEnabled;
	
	private String basePath = "/";
	
	private String apiVersion = "1.0";

	public ApplicationConfiguration() {
	}

	public ApplicationConfiguration(String name) {
		super(name);
	}

	/**
	 * @return the packagesToScan
	 */
	public List<String> getPackagesToScan() {
		return packagesToScan;
	}

	/**
	 * @param packagesToScan the packagesToScan to set
	 */
	public void setPackagesToScan(List<String> packagesToScan) {
		this.packagesToScan = packagesToScan;
	}

	/**
	 * @return the databaseConfiguration
	 */
	@JsonProperty("db")
	public DatabaseConfiguration getDatabaseConfiguration() {
		return databaseConfiguration;
	}

	/**
	 * @param databaseConfiguration the databaseConfiguration to set
	 */
	public void setDatabaseConfiguration(DatabaseConfiguration databaseConfiguration) {
		this.databaseConfiguration = databaseConfiguration;
	}

	/**
	 * @return the instrumentationEnabled
	 */
	public boolean isInstrumentationEnabled() {
		return instrumentationEnabled;
	}

	/**
	 * @param instrumentationEnabled the instrumentationEnabled to set
	 */
	public void setInstrumentationEnabled(boolean instrumentationEnabled) {
		this.instrumentationEnabled = instrumentationEnabled;
	}

	/**
	 * @return the basePath
	 */
	public String getBasePath() {
		return basePath;
	}

	/**
	 * @param basePath the basePath to set
	 */
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	/**
	 * @return the apiVersion
	 */
	public String getApiVersion() {
		return apiVersion;
	}

	/**
	 * @param apiVersion the apiVersion to set
	 */
	public void setApiVersion(String apiVersion) {
		this.apiVersion = apiVersion;
	}

}
