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
public class AdminBundle extends ContainerAdapter implements Bundle<AdminBundleConfiguration> {

	public void init(Container container, AdminBundleConfiguration configuration) {
		container.registerListener(this);
	}

	public void start() {
	}

	public void stop() {
	}
	
	@Override
	public void postMount(Application<ApplicationConfiguration> application) {
		ApplicationRoutes.instance.addApplication(application);
	}

	@Override
	public void postUnMount(Application<ApplicationConfiguration> application) {
		ApplicationRoutes.instance.removeApplication(application);
	}

	@Override
	public int getOrder() {
		return 1;
	}
}
