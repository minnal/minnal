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
import org.minnal.instrument.filter.ResponseTransformationFilter;
import org.minnal.instrument.util.MinnalModule;

/**
 * @author ganeshs
 *
 */
public class InstrumentationBundle extends ContainerAdapter implements Bundle<InstrumentationBundleConfiguration> {
	
	public void init(Container container, InstrumentationBundleConfiguration configuration) {
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

	@Override
	public void postMount(Application<ApplicationConfiguration> application) {
		application.addFilter(new ResponseTransformationFilter(application.getConfiguration().getResponsePropertiesToExclude()));
		application.getObjectMapper().registerModule(new MinnalModule());
		
		if (application.getConfiguration().isInstrumentationEnabled()) {
			createApplicationEnhancer(application).enhance();
		}
	}
	
	protected ApplicationEnhancer createApplicationEnhancer(Application<ApplicationConfiguration> application) {
		return new MinnalApplicationEnhancer(application, new DefaultNamingStrategy());
	}
	
	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
