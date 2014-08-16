/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.config.ServerConfiguration;
import org.minnal.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ContainerConfigGenerator extends AbstractGenerator {
	
	private File file;
	
	private ContainerConfiguration configuration;
	
	private Map<String, String> mounts = new HashMap<String, String>();
	
	private static final Logger logger = LoggerFactory.getLogger(ContainerConfigGenerator.class);
	
	/**
	 * @param resourcesDir
	 */
	public ContainerConfigGenerator(File baseDir) {
		super(baseDir);
		addGenerator(new ApplicationSpiGenerator(baseDir));
	}
	
	public void addApplication(String applicationClass, String mountPath) {
		mounts.put(applicationClass, mountPath);
		generatorFor(ApplicationSpiGenerator.class).addApplication(applicationClass);
	}
	
	@Override
	public void init() {
		super.init();
		file = new File(getMetaInfFolder(true), "container.yml");
	
		if (! file.exists()) {
			configuration = createContainerConfiguration();
		} else {
			configuration = deserializeFrom(file, Serializer.DEFAULT_YAML_SERIALIZER, ContainerConfiguration.class);
		}
	}
	
	private ContainerConfiguration createContainerConfiguration() {
		ContainerConfiguration configuration = new ContainerConfiguration("My Container");
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.addConnectorConfiguration(new ConnectorConfiguration(8080, Scheme.http, null, 2));
		configuration.setServerConfiguration(serverConfiguration);
		configuration.setMounts(mounts);
		return configuration;
	}

	@Override
	public void generate() {
		logger.info("Creating the container config file {}", file.getAbsolutePath());
		super.generate();
		serializeTo(file, configuration, Serializer.DEFAULT_YAML_SERIALIZER);
	}
}
