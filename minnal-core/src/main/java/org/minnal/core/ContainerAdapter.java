/**
 * 
 */
package org.minnal.core;

import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public abstract class ContainerAdapter implements ContainerLifecycleListener {

	public void beforeStart(Container container) {
	}

	public void beforeStop(Container container) {
	}

	public void afterStart(Container container) {
	}

	public void afterStop(Container container) {
	}

	@Override
	public void postMount(Application<ApplicationConfiguration> application) {
	}
	
	@Override
	public void postUnMount(Application<ApplicationConfiguration> application) {
	}
	
	@Override
	public void preMount(Application<ApplicationConfiguration> application) {
	}
	
	@Override
	public void preUnMount(Application<ApplicationConfiguration> application) {
	}

}
