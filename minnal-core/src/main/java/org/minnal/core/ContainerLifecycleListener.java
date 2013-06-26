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
	 * Invoked before mounting the application.
	 * <pre>
	 * Can be used as an entry point by bundles to alter the application
	 *  
	 * @param application
	 */
	void preMount(Application<ApplicationConfiguration> application);
	
	/**
	 * Invoked when an application is successfully mounted. The application would have been initialized by then.
	 *  
	 * @param application
	 */
	void postMount(Application<ApplicationConfiguration> application);
	
	/**
	 * Invoked before unmounting the application.
	 * <pre>
	 * Can be used as a hook point by bundles to alter the application
	 *  
	 * @param application
	 */
	void preUnMount(Application<ApplicationConfiguration> application);
	
	/**
	 * Invoked when an application is successfully unmounted. The application would have been destroyed by then.
	 *  
	 * @param application
	 */
	void postUnMount(Application<ApplicationConfiguration> application);

}
