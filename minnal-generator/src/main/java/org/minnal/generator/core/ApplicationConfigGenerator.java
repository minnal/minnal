/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.IOUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.minnal.core.MinnalException;
import org.minnal.core.config.ApplicationConfiguration;
import org.minnal.core.config.DatabaseConfiguration;
import org.minnal.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationConfigGenerator implements Generator {
	
	private String resourcesDir;
	
	private String applicationName;
	
	private File file;
	
	private String packageName;
	
	private boolean enableInstrumentation;
	
	private ApplicationConfiguration configuration;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationConfigGenerator.class);
	
	/**
	 * @param resourcesDir
	 * @param applicationName
	 * @param packageName
	 * @param enableInstrumentation	
	 */
	public ApplicationConfigGenerator(String resourcesDir, String applicationName, String packageName, boolean enableInstrumentation) {
		this.resourcesDir = resourcesDir;
		this.applicationName = applicationName;
		this.packageName = packageName;
		this.enableInstrumentation = enableInstrumentation;
	}
	
	public void loadFile() {
		File dir = new File(resourcesDir);
		if (! dir.exists()) {
			throw new MinnalException("Resources directory " + this.resourcesDir + " doesn't exist");
		}
		File metaInf = new File(dir, "META-INF");
		if (! metaInf.exists()) {
			logger.info("Creating the META-INF folder under {}", resourcesDir);
			metaInf.mkdirs();
		}
		file = new File(metaInf, applicationName.toLowerCase() + ".yml");
	
		try {
			if (! file.exists()) {
				file.createNewFile();
				configuration = createApplicationConfiguration();
			} else {
				configuration = Serializer.DEFAULT_YAML_SERIALIZER.deserialize(ChannelBuffers.wrappedBuffer(IOUtil.toByteArray(new FileInputStream(file))), ApplicationConfiguration.class);
			}
		} catch (Exception e) {
			throw new MinnalException("Failed while creating the file " + file.getAbsolutePath(), e);
		}
	}
	
	private ApplicationConfiguration createApplicationConfiguration() {
		ApplicationConfiguration configuration = new ApplicationConfiguration(applicationName);
		DatabaseConfiguration databaseConfiguration = new DatabaseConfiguration();
		databaseConfiguration.setDriverClass("org.hsqldb.jdbc.JdbcDriver");
		databaseConfiguration.setIdleConnectionTestPeriod(300);
		databaseConfiguration.setMinSize(5);
		databaseConfiguration.setMaxSize(10);
		databaseConfiguration.setPackagesToScan(Arrays.asList(packageName));
		databaseConfiguration.setUrl("jdbc:hsqldb:mem:.");
		databaseConfiguration.setUsername("sa");
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("hibernate.ejb.naming_strategy", "org.hibernate.cfg.ImprovedNamingStrategy");
		properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
		properties.put("hibernate.current_session_context_class", "thread");
		properties.put("hibernate.hbm2ddl.auto", "crteate-drop");
		databaseConfiguration.setProviderProperties(properties);
		configuration.setPackagesToScan(Arrays.asList(packageName));
		configuration.setDatabaseConfiguration(databaseConfiguration);
		configuration.setInstrumentationEnabled(enableInstrumentation);
		return configuration;
	}

	@Override
	public void generate() {
		loadFile();
		logger.info("Creating the application config file {}", file.getAbsolutePath());
		ChannelBuffer buffer = Serializer.DEFAULT_YAML_SERIALIZER.serialize(configuration);
		try {
			FileWriter writer = new FileWriter(file);
			IOUtil.copy(new ChannelBufferInputStream(buffer), writer);
		} catch (Exception e) {
			throw new MinnalException("Failed while writing the config file " + file.getAbsolutePath(), e);
		}
	}
}
