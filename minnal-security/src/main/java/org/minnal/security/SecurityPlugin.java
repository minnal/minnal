/**
 * 
 */
package org.minnal.security;

import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.minnal.core.Application;
import org.minnal.core.Plugin;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.security.config.SecurityAware;
import org.minnal.security.config.SecurityConfiguration;
import org.minnal.security.filter.AuthenticationFilter;
import org.minnal.security.filter.CallbackFilter;
import org.minnal.security.filter.SecurityContextFilter;
import org.pac4j.core.client.Client;
import org.pac4j.core.client.Clients;

/**
 * @author ganeshs
 *
 */
public class SecurityPlugin implements Plugin {
	
	private Clients clients;
	
	/**
	 * @param callbackUrl
	 * @param clients
	 */
	public SecurityPlugin(String callbackUrl, Client... clients) {
		this.clients = new Clients(callbackUrl, clients);
	}
	
	/**
	 * @param clients
	 */
	public SecurityPlugin(Clients clients) {
		this.clients = clients;
	}

	@Override
	public void init(Application<? extends ApplicationConfiguration> application) {
		ApplicationConfiguration applicationConfiguration = application.getConfiguration();
		if (! (applicationConfiguration instanceof SecurityAware)) {
			return;
		}
		SecurityConfiguration configuration = ((SecurityAware) applicationConfiguration).getSecurityConfiguration();
		clients.init();
		application.addFilter(new CallbackFilter(clients, configuration));
		application.addFilter(new AuthenticationFilter(clients, configuration));
		application.addFilter(new SecurityContextFilter(configuration));
		application.getResourceConfig().register(RolesAllowedDynamicFeature.class);
	}

	@Override
	public void destroy() {
	}

}
