/**
 * 
 */
package org.minnal.security.config;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author ganeshs
 *
 */
public interface SecurityAware {

	@JsonProperty("security")
	SecurityConfiguration getSecurityConfiguration();
	
	void setSecurityConfiguration(SecurityConfiguration configuration);
}
