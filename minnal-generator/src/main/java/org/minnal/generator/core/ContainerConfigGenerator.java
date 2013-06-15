/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.plexus.util.IOUtil;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.minnal.core.MinnalException;
import org.minnal.core.config.ConnectorConfiguration;
import org.minnal.core.config.ConnectorConfiguration.Scheme;
import org.minnal.core.config.ContainerConfiguration;
import org.minnal.core.config.ServerConfiguration;
import org.minnal.core.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

/**
 * @author ganeshs
 *
 */
public class ContainerConfigGenerator implements Generator {
	
	private String resourcesDir;
	
	private File file;
	
	private ContainerConfiguration configuration;
	
	private Map<String, String> mounts = new HashMap<String, String>();
	
	private ApplicationSpiGenerator spiGenerator;
	
	private static final Logger logger = LoggerFactory.getLogger(ContainerConfigGenerator.class);
	
	/**
	 * @param resourcesDir
	 */
	public ContainerConfigGenerator(String resourcesDir) {
		this.resourcesDir = resourcesDir;
		spiGenerator = new ApplicationSpiGenerator(resourcesDir);
	}
	
	public void addApplication(String applicationClass, String mountPath) {
		mounts.put(applicationClass, mountPath);
		spiGenerator.addApplication(applicationClass);
	}
	
	public void loadFile() {
		File dir = new File(resourcesDir);
		if (! dir.exists()) {
			throw new MinnalException("Resources directory " + this.resourcesDir + " doesn't exist");
		}
		File metaInf = new File(dir, "META-INF");
		if (! metaInf.exists()) {
			metaInf.mkdirs();
		}
		file = new File(metaInf, "container.yml");
	
		try {
			if (! file.exists()) {
				file.createNewFile();
				createContainerConfiguration();
			} else {
				configuration = Serializer.DEFAULT_YAML_SERIALIZER.deserialize(ChannelBuffers.wrappedBuffer(IOUtil.toByteArray(new FileInputStream(file))), ContainerConfiguration.class);
			}
		} catch (Exception e) {
			throw new MinnalException("Failed while creating the file " + file.getAbsolutePath());
		}
	}
	
	private void createContainerConfiguration() {
		configuration = new ContainerConfiguration("My Container");
		configuration.setDefaultMediaType(MediaType.JSON_UTF_8);
		configuration.addSerializer(MediaType.JSON_UTF_8, Serializer.getSerializer(MediaType.JSON_UTF_8));
		configuration.addSerializer(MediaType.XML_UTF_8, Serializer.getSerializer(MediaType.XML_UTF_8));
		configuration.addSerializer(MediaType.FORM_DATA, Serializer.getSerializer(MediaType.FORM_DATA));
		configuration.addSerializer(MediaType.PLAIN_TEXT_UTF_8, Serializer.getSerializer(MediaType.PLAIN_TEXT_UTF_8));
		ServerConfiguration serverConfiguration = new ServerConfiguration();
		serverConfiguration.addConnectorConfiguration(new ConnectorConfiguration(8080, Scheme.http, null, 2));
		configuration.setServerConfiguration(serverConfiguration);
		configuration.setMounts(mounts);
	}

	@Override
	public void generate() {
		loadFile();
		logger.info("Creating the container config file {}", file.getAbsolutePath());
		ChannelBuffer buffer = Serializer.DEFAULT_YAML_SERIALIZER.serialize(configuration);
		try {
			FileWriter writer = new FileWriter(file);
			IOUtil.copy(new ChannelBufferInputStream(buffer), writer);
		} catch (Exception e) {
			throw new MinnalException("Failed while writing the config file " + file.getAbsolutePath(), e);
		}
		spiGenerator.generate();
	}
}
