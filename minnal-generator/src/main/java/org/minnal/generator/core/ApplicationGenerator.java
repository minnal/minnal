/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.javalite.common.Inflector;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ApplicationGenerator implements Generator {
	
	private String srcDir;
	
	private String applicationClassName;
	
	private String applicationConfigClassName;
	
	private String packageName;
	
	private static Template createApplicationConfigTemplate;
	
	private static Template createApplicationTemplate;
	
	private List<Generator> generators = new ArrayList<Generator>();
	
	private static final Logger logger = LoggerFactory.getLogger(ApplicationGenerator.class);
	
	static {
		logger.debug("Loading the velocity templates");
		VelocityEngine ve = new VelocityEngine();
		ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath"); 
		ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		createApplicationConfigTemplate = ve.getTemplate("META-INF/templates/create_application_configuration.vm");
		createApplicationTemplate = ve.getTemplate("META-INF/templates/create_application.vm");
	}
	
	/**
	 * @param srcDir
	 * @param applicationName
	 * @param packageName
	 */
	public ApplicationGenerator(String srcDir, String resourcesDir, String applicationName, String packageName) {
		this.srcDir = srcDir;
		this.packageName = packageName;
		this.applicationClassName = Inflector.capitalize(applicationName + "Application");
		this.applicationConfigClassName = Inflector.capitalize(applicationName + "Configuration");
		generators.add(new ApplicationConfigGenerator(resourcesDir, applicationName, packageName, true));
	}

	/**
	 * @return the applicationClassName
	 */
	public String getApplicationClassName() {
		return applicationClassName;
	}

	/**
	 * @return the applicationConfigClassName
	 */
	public String getApplicationConfigClassName() {
		return applicationConfigClassName;
	}

	/**
	 * @return the packageName
	 */
	public String getPackageName() {
		return packageName;
	}

	@Override
	public void generate() {
		createApplicationConfigClass();
		createApplicationClass();
		for (Generator generator : generators) {
			generator.generate();
		}
	}
	
	protected void createApplicationClass() {
		VelocityContext context = new VelocityContext();
		context.put("packageName", packageName);
		context.put("applicationClassName", applicationClassName);
		context.put("applicationConfigClassName", applicationConfigClassName);
		createFile(createApplicationTemplate, context, new File(createPackage(), applicationClassName + ".java"));
	}
	
	protected void createApplicationConfigClass() {
		VelocityContext context = new VelocityContext();
		context.put("packageName", packageName);
		context.put("applicationConfigClassName", applicationConfigClassName);
		createFile(createApplicationConfigTemplate, context, new File(createPackage(), applicationConfigClassName + ".java"));
	}
	
	protected void createFile(Template template, VelocityContext context, File file) {
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

	private File createPackage() {
		File pkg = new File(srcDir, packageName.replace('.', '/'));
		if (! pkg.exists()) {
			pkg.mkdirs();
		}
		return pkg;
	}
}
