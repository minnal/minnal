/**
 * 
 */
package org.minnal.jpa;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.minnal.core.config.DatabaseConfiguration;

/**
 * @author ganeshs
 *
 */
public class JerseyApplicationEventListener implements ApplicationEventListener {
	
	private DatabaseConfiguration configuration;
	
	/**
	 * @param configuration
	 */
	public JerseyApplicationEventListener(DatabaseConfiguration configuration) {
		this.configuration = configuration;
	}

	@Override
	public void onEvent(ApplicationEvent event) {
	}

	@Override
	public RequestEventListener onRequest(RequestEvent requestEvent) {
		return new OpenSessionInViewFilter(configuration);
	}

}
