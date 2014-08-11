/**
 * 
 */
package org.minnal.examples.oms;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public class OrderConfiguration extends ApplicationConfiguration implements SecurityAware {
	
	@JsonProperty("security")
	private SecurityConfiguration securityConfiguration;
	
	private boolean enableSecurity;

	@Override
	public SecurityConfiguration getSecurityConfiguration() {
		return securityConfiguration;
	}

	@Override
	public void setSecurityConfiguration(SecurityConfiguration configuration) {
		this.securityConfiguration = configuration;
	}

	/**
	 * @return the enableSecurity
	 */
	public boolean isEnableSecurity() {
		return enableSecurity;
	}

	/**
	 * @param enableSecurity the enableSecurity to set
	 */
	public void setEnableSecurity(boolean enableSecurity) {
		this.enableSecurity = enableSecurity;
	}

}
