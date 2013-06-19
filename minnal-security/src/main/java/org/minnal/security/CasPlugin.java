/**
 * 
 */
package org.minnal.security;

import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.cas.CasFilter;
import org.minnal.security.filter.cas.CasProxyCallbackFilter;
import org.minnal.security.filter.cas.SingleSignOutFilter;

/**
 * @author ganeshs
 *
 */
public class CasPlugin implements Plugin {
	
	private SecurityConfiguration configuration;
	
	public CasPlugin(SecurityConfiguration configuration) {
		this.configuration = configuration;
	}

	public void init(Application<? extends ApplicationConfiguration> application) {
		application.addFilter(new SingleSignOutFilter(configuration));
		application.addFilter(new CasProxyCallbackFilter(configuration));
		application.addFilter(new CasFilter(configuration));
	}

	public void destroy() {
	}
}
