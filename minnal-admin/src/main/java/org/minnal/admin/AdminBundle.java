/**
 * 
 */
package org.minnal.admin;

import org.minnal.core.Application;
import org.minnal.core.Bundle;
import org.minnal.core.Container;
import org.minnal.core.ContainerAdapter;
import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public class AdminBundle extends ContainerAdapter implements Bundle {

	public void init(Container container) {
		container.registerListener(this);
	}

	public void start() {
	}

	public void stop() {
	}

	public void onMount(Application<ApplicationConfiguration> application, String mountUrl) {
		ApplicationRoutes.instance.addApplication(application);
	}

	public void onUnMount(Application<ApplicationConfiguration> application, String mountUrl) {
		ApplicationRoutes.instance.removeApplication(application);
	}

}
