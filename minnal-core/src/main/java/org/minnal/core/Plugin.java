/**
 * 
 */
package org.minnal.core;

import org.minnal.core.config.ApplicationConfiguration;


/**
 * @author ganeshs
 *
 */
public interface Plugin {

	void init(Application<? extends ApplicationConfiguration> application);
	
	void destroy();
}
