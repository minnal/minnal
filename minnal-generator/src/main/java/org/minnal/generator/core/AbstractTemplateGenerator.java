/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author ganeshs
 *
 */
public abstract class AbstractTemplateGenerator extends AbstractGenerator {

	private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateGenerator.class);
	
	protected static VelocityEngine engine;
	
	static {
		logger.debug("Loading the velocity templates");
		Properties properties = new Properties();
		properties.put("runtime.log.logsystem.class", "org.minnal.core.util.Slf4jLogChute");
		engine = new VelocityEngine(properties);
		engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
	}
	
	public AbstractTemplateGenerator(File baseDir) {
		super(baseDir);
	}
	
	protected void writeFile(Template template, VelocityContext context, File file) {
		logger.info("Creating the file {}", file.getAbsolutePath());
		Writer writer = null;
		try {
			writer = new FileWriter(file);
			template.merge(context, writer);
		} catch (IOException e) {
			throw new MinnalException(e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e) {
					// Ignore
				}
			}
		}
	}
}
