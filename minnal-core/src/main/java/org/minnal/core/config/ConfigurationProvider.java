/**
 * 
 */
package org.minnal.core.config;

/**
 * @author ganeshs
 *
 */
public abstract class ConfigurationProvider {
	
	private static DefaultConfigurationProvider defaultConfigProvider = new DefaultConfigurationProvider();

	/**
	 * Provides the configuration from the given path
	 * 
	 * @param clazz
	 * @param path
	 * @return
	 */
	public abstract <T extends Configuration> T provide(Class<T> clazz, String path);
	
	/**
	 * Provides the configuration by computing the config path from the configuration class name
	 * @param clazz
	 * @return
	 */
	public abstract <T extends Configuration> T provide(Class<T> clazz);
	
	public static ConfigurationProvider getDefault() {
		return defaultConfigProvider;
	}
}
