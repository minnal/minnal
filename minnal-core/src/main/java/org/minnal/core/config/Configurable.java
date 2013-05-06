/**
 * 
 */
package org.minnal.core.config;

/**
 * @author ganeshs
 *
 */
public interface Configurable<T extends Configuration> {

	void init(T configuration);
}
