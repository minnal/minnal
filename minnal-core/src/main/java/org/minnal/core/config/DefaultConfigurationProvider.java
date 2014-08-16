/**
 * 
 */
package org.minnal.core.config;

import java.io.InputStream;

import org.minnal.core.MinnalException;
import org.minnal.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The default configuration provider implementation. Makes use of jackson yaml plugin to transform config file to configuration instance
 * 
 * @author ganeshs
 *
 */
public class DefaultConfigurationProvider extends ConfigurationProvider {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultConfigurationProvider.class);
	
	public DefaultConfigurationProvider() {
	}

	public <T extends Configuration> T provide(Class<T> clazz, String path) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		try {
			return Serializer.DEFAULT_YAML_SERIALIZER.deserialize(is, clazz);
		} catch (Exception e) {
			logger.error("Failed while reading the config file - " + path, e);
			throw new MinnalException(e);
		}
	}

	public <T extends Configuration> T provide(Class<T> clazz) {
		String classname = clazz.getSimpleName();
		if (classname.endsWith("Configuration")) {
			classname = classname.substring(0, classname.length() - "Configuration".length());
		}
		return provide(clazz, "META-INF/" + classname.toLowerCase() + ".yml");
	}

}
