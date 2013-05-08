/**
 * 
 */
package org.minnal.instrument;

import org.activejpa.enhancer.ActionJpaAgentLoader;
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
		ActionJpaAgentLoader.loadAgent();
		container.registerListener(this);
	}

	public void start() {
	}

	public void stop() {
	}

	public void onMount(Application<ApplicationConfiguration> application, String mountPath) {
		ApplicationEnhancer enhancer = new ApplicationEnhancer(application);
		enhancer.enhance();
	}
}
