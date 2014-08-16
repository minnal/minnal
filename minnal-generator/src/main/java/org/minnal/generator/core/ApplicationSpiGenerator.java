/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.minnal.core.Application;
import org.minnal.utils.serializer.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationSpiGenerator extends AbstractGenerator {
	
	private List<String> applications = new ArrayList<String>();
	
	private File file;
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationSpiGenerator.class);
	
	/**
	 * @param applications
	 */
	public ApplicationSpiGenerator(File baseDir) {
		super(baseDir);
	}
	
	@Override
	public void init() {
		super.init();
		file = new File(getServicesFolder(true), Application.class.getName());
	}
	
	public void addApplication(String application) {
		this.applications.add(application);
	}

	@Override
	public void generate() {
		logger.info("Creating the application spi file {}", file.getAbsolutePath());
		StringWriter writer = new StringWriter();
		for (String application : applications) {
			writer.append(application).append("\n");
		}
		serializeTo(file, writer.toString(), Serializer.DEFAULT_TEXT_SERIALIZER);
	}

}
