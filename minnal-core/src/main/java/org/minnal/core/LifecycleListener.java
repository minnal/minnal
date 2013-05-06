/**
 * 
 */
package org.minnal.core;

/**
 * @author ganeshs
 *
 */
public interface LifecycleListener<T extends Lifecycle> {
	
	void beforeStart(T lifecycle);
	
	void beforeStop(T lifecycle);

	void afterStart(T lifecycle);
	
	void afterStop(T lifecycle);
}
