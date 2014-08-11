/**
 * 
 */
package org.minnal.security.config;


/**
 * @author ganeshs
 *
 */
public interface SecurityAware {

	SecurityConfiguration getSecurityConfiguration();
	
	void setSecurityConfiguration(SecurityConfiguration configuration);
}
