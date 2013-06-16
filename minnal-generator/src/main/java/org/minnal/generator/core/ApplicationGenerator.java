/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.javalite.common.Inflector;

/**
 * @author ganeshs
 *
 */
public class ApplicationGenerator extends AbstractTemplateGenerator {
	
	private static Template createApplicationConfigTemplate = engine.getTemplate("META-INF/templates/create_application_configuration.vm");
	
	private static Template createApplicationTemplate = engine.getTemplate("META-INF/templates/create_application.vm");
	
	/**
	 * @param baseDir
	 */
	public ApplicationGenerator(File baseDir) {
		super(baseDir);
		addGenerator(new ApplicationConfigGenerator(baseDir, true));
	}
	
	@Override
	public void init() {
		super.init();
	}

	@Override
	public void generate() {
		super.generate();
		createApplicationConfigClass();
		createApplicationClass();
	}
	
	protected void createApplicationClass() {
		String applicationClass = Inflector.shortName(getApplicationClassName());
		String applicationConfigClass = Inflector.shortName(getApplicationConfigClassName());
		VelocityContext context = new VelocityContext();
		context.put("packageName", getBasePackage());
		context.put("applicationClassName", applicationClass);
		context.put("applicationConfigClassName", applicationConfigClass);
		writeFile(createApplicationTemplate, context, new File(createPackage(getBasePackage()), applicationClass + ".java"));
	}
	
	protected void createApplicationConfigClass() {
		String applicationConfigClass = Inflector.shortName(getApplicationConfigClassName());
		VelocityContext context = new VelocityContext();
		context.put("packageName", getBasePackage());
		context.put("applicationConfigClassName", applicationConfigClass);
		writeFile(createApplicationConfigTemplate, context, new File(createPackage(getBasePackage()), applicationConfigClass + ".java"));
	}
}
