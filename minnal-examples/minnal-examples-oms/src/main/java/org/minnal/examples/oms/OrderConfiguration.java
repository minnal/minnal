/**
 * 
 */
package org.minnal.examples.oms;

import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;

/**
 * @author ganeshs
 *
 */
public class OrderConfiguration extends ApplicationConfiguration implements SecurityAware {
	
	private SecurityConfiguration securityConfiguration;

	@Override
	public SecurityConfiguration getSecurityConfiguration() {
		return securityConfiguration;
	}

	@Override
	public void setSecurityConfiguration(SecurityConfiguration configuration) {
		this.securityConfiguration = configuration;
	}

}
