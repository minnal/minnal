/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class LoggerConfigGenerator extends AbstractTemplateGenerator {
	
	private File file;
	
	private static Template createLoggerTemplate = engine.getTemplate("META-INF/templates/log4j_properties.vm");
	
	private static final Logger logger = LoggerFactory.getLogger(LoggerConfigGenerator.class);
	
	public LoggerConfigGenerator(File baseDir) {
		super(baseDir);
	}
	
	@Override
	public void init() {
		super.init();
		file = new File(getResourcesFolder(true), "log4j.properties");
	}

	@Override
	public void generate() {
		logger.info("Creating the file {}", file.getAbsolutePath());
		writeFile(createLoggerTemplate, new VelocityContext(), file);
	}

}
