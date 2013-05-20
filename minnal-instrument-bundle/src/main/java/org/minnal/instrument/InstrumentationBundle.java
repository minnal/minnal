/**
 * 
 */
package org.minnal.instrument;

import org.activejpa.enhancer.ActiveJpaAgentLoader;
import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class InstrumentationBundle extends ContainerAdapter implements Bundle {
	
	public void init(Container container) {
		getActiveJpaAgentLoader().loadAgent();
		container.registerListener(this);
	}
	
	protected ActiveJpaAgentLoader getActiveJpaAgentLoader() {
		return ActiveJpaAgentLoader.instance();
	}

	public void start() {
	}

	public void stop() {
	}

	public void onMount(Application<ApplicationConfiguration> application, String mountPath) {
		createApplicationEnhancer(application).enhance();
	}
	
	protected ApplicationEnhancer createApplicationEnhancer(Application<ApplicationConfiguration> application) {
		return new ApplicationEnhancer(application);
	}
}
