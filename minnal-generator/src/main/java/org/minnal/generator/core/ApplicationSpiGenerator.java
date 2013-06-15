/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import org.minnal.core.Application;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationSpiGenerator implements Generator {
	
	private List<String> applications = new ArrayList<String>();
	
	private String resourcesDir;
	
	private File file;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationSpiGenerator.class);
	
	/**
	 * @param applications
	 */
	public ApplicationSpiGenerator(String resourcesDir) {
		this.resourcesDir = resourcesDir;
	}
	
	public void loadFile() {
		File dir = new File(resourcesDir);
		if (! dir.exists()) {
			throw new MinnalException("Resources directory " + this.resourcesDir + " doesn't exist");
		}
		File services = new File(dir, "META-INF/services");
		if (! services.exists()) {
			logger.info("Creating the services folder under META-INF");
			services.mkdirs();
		}
		file = new File(services, Application.class.getName());
	
		try {
			if (! file.exists()) {
				file.createNewFile();
			}
		} catch (Exception e) {
			throw new MinnalException("Failed while creating the file " + file.getAbsolutePath(), e);
		}
	}
	
	public void addApplication(String application) {
		this.applications.add(application);
	}

	@Override
	public void generate() {
		loadFile();
		logger.info("Creating the application spi file {}", file.getAbsolutePath());
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
			for (String application : applications) {
				writer.append(application).append("\n");
			}
		} catch (Exception e) {
			throw new MinnalException("Failed while creating the file " + file.getAbsolutePath(), e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					// ignore
				}
			}
		}
	}

}
