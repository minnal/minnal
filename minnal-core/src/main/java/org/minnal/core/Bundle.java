/**
 * 
 */
package org.minnal.core;


/**
 * @author ganeshs
 *
 */
public interface Bundle<T extends BundleConfiguration> {
	
	void init(Container Container, T configuration);
	
	void start();
	
	void stop();
	
	int getOrder();
}
