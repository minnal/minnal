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

	public void onMount(Application<ApplicationConfiguration> application, String mountUrl) {
	}

	public void onUnMount(Application<ApplicationConfiguration> application, String mountUrl) {
	}

}
