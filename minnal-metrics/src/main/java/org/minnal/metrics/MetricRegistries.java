/**
 * 
 */
package org.minnal.metrics;

import java.util.HashMap;
import java.util.Map;

import org.minnal.core.Application;
import org.minnal.core.config.ApplicationConfiguration;

import com.codahale.metrics.MetricRegistry;

/**
 * @author ganeshs
 *
 */
public final class MetricRegistries {

	private static Map<String, MetricRegistry> registries = new HashMap<String, MetricRegistry>();
	
	/**
	 * @param application
	 */
	public static void addRegistry(Application<ApplicationConfiguration> application, MetricRegistry registry) {
		String applicationName = application.getConfiguration().getName();
		if (registries.containsKey(applicationName)) {
			return;
		}
		registries.put(applicationName, registry);
	}
	
	/**
	 * @param application
	 */
	public static void removeRegistry(Application<ApplicationConfiguration> application) {
		String applicationName = application.getConfiguration().getName();
		if (! registries.containsKey(applicationName)) {
			return;
		}
		registries.remove(applicationName);
	}
	
	/**
	 * @param application
	 */
	public static MetricRegistry getRegistry(String applicationName) {
		return registries.get(applicationName);
	}
}
