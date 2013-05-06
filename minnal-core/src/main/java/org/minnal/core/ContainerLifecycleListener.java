/**
 * 
 */
package org.minnal.core;

import org.minnal.core.config.ApplicationConfiguration;

/**
 * @author ganeshs
 *
 */
public interface ContainerLifecycleListener extends LifecycleListener<Container> {

	/**
	 * Invoked when an application is successfully mounted. The application would have been initialized by then.
	 * <pre>
	 * Can be used as an entry point by bundles to alter the application
	 *  
	 * @param application
	 * @param mountUrl
	 */
	void onMount(Application<ApplicationConfiguration> application, String mountUrl);
	
	/**
	 * Invoked when an application is successfully unmounted. The application would have been destroyed by then.
	 *  
	 * @param application
	 * @param mountUrl
	 */
	void onUnMount(Application<ApplicationConfiguration> application, String mountUrl);
}
