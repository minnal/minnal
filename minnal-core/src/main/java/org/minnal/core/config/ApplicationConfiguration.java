/**
 * 
 */
package org.minnal.core.config;

import java.util.List;


/**
 * @author ganeshs
 *
 */
public class ApplicationConfiguration extends Configuration {
	
	private List<String> packagesToScan;

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

}
