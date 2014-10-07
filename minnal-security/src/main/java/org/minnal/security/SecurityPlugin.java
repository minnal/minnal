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
import org.minnal.security.filter.AuthenticationListener;
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
	
	private AuthenticationListener listener;
	
	/**
	 * @param callbackUrl
	 * @param clients
	 */
	public SecurityPlugin(String callbackUrl, Client... clients) {
		this.clients = new Clients(callbackUrl, clients);
	}
	
	/**
     * @param callbackUrl
     * @param clients
     */
    public SecurityPlugin(String callbackUrl, AuthenticationListener listener, Client... clients) {
        this.clients = new Clients(callbackUrl, clients);
        this.listener = listener;
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
		
		CallbackFilter callbackFilter = new CallbackFilter(clients, configuration);
		callbackFilter.registerListener(listener);
		application.addFilter(callbackFilter);
		application.addFilter(new AuthenticationFilter(clients, configuration));
		application.addFilter(new SecurityContextFilter(configuration));
		application.getResourceConfig().register(RolesAllowedDynamicFeature.class);
	}

	@Override
	public void destroy() {
	}

}
