/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.javalite.common.Inflector;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationConfigGenerator extends AbstractGenerator {
	
	private File file;
	
	private boolean enableInstrumentation;
	
	private ApplicationConfiguration configuration;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigGenerator.class);
	
	/**
	 * @param baseDir
	 * @param enableInstrumentation	
	 */
	public ApplicationConfigGenerator(File baseDir, boolean enableInstrumentation) {
		super(baseDir);
		this.enableInstrumentation = enableInstrumentation;
	}
	
	@Override
	public void init() {
		super.init();
		file = new File(getMetaInfFolder(true), applicationName.toLowerCase() + ".yml");
		if (! file.exists()) {
			configuration = createApplicationConfiguration();
		} else {
			configuration = deserializeFrom(file, Serializer.DEFAULT_YAML_SERIALIZER, ApplicationConfiguration.class);
		}
	}
	
	private ApplicationConfiguration createApplicationConfiguration() {
		ApplicationConfiguration configuration = new ApplicationConfiguration(Inflector.tableize(applicationName));
		DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
		databaseConfiguration.setDriverClass("org.hsqldb.jdbcDriver");
		databaseConfiguration.setIdleConnectionTestPeriod(300);
		databaseConfiguration.setMinSize(5);
		databaseConfiguration.setMaxSize(10);
		databaseConfiguration.setPackagesToScan(Arrays.asList(getBasePackage()));
		databaseConfiguration.setUrl("jdbc:hsqldb:mem:.");
		databaseConfiguration.setUsername("sa");
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
		properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		properties.put("hibernate.current_session_context_class", "thread");
		properties.put("hibernate.hbm2ddl.auto", "create-drop");
		databaseConfiguration.setProviderProperties(properties);
		configuration.setPackagesToScan(Arrays.asList(getBasePackage()));
		configuration.setDatabaseConfiguration(databaseConfiguration);
		configuration.setInstrumentationEnabled(enableInstrumentation);
		return configuration;
	}

	@Override
	public void generate() {
		logger.info("Creating the application config file {}", file.getAbsolutePath());
		serializeTo(file, configuration, Serializer.DEFAULT_YAML_SERIALIZER);
	}
}
