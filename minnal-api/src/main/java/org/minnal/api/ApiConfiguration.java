/**
 * 
 */
package org.minnal.api;

import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class ApiConfiguration extends ApplicationConfiguration {

	private String swaggerVersion = "1.1";

	/**
	 * @return the swaggerVersion
	 */
	public String getSwaggerVersion() {
		return swaggerVersion;
	}

	/**
	 * @param swaggerVersion the swaggerVersion to set
	 */
	public void setSwaggerVersion(String swaggerVersion) {
		this.swaggerVersion = swaggerVersion;
	}
}
