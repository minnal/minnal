/**
 * 
 */
package org.minnal.api;

import org.minnal.core.BundleConfiguration;

/**
 * @author ganeshs
 *
 */
public class ApiBundleConfiguration extends BundleConfiguration {

	private boolean enableCors = true;

	/**
	 * @return the enableCors
	 */
	public boolean isEnableCors() {
		return enableCors;
	}

	/**
	 * @param enableCors the enableCors to set
	 */
	public void setEnableCors(boolean enableCors) {
		this.enableCors = enableCors;
	}
}
