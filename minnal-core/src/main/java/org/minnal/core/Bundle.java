/**
 * 
 */
package org.minnal.core;


/**
 * @author ganeshs
 *
 */
public interface Bundle {
	
	void init(Container Container);
	
	void start();
	
	void stop();
	
	int getOrder();
}
