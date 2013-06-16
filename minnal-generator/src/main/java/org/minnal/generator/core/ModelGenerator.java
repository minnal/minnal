/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.javalite.common.Inflector;
import org.minnal.generator.CommandGenerateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class ModelGenerator extends AbstractTemplateGenerator {

	private CommandGenerateModel model;
	
	private static Template createModelTemplate = engine.getTemplate("META-INF/templates/create_model.vm");
	
	private static final Logger logger = LoggerFactory.getLogger(ModelGenerator.class);

	/**
	 * @param model
	 */
	public ModelGenerator(CommandGenerateModel model) {
		super(new File(model.getProjectDir()));
		this.model = model;
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void generate() {
		logger.info("Generating the model class {} under the package {}", model.getName(), getDomainPackage());
		VelocityContext context = new VelocityContext();
		context.put("inflector", Inflector.class);
		context.put("model", model);
		context.put("packageName", getDomainPackage());
		
		writeFile(createModelTemplate, context, new File(createPackage(getDomainPackage()), model.getName() + ".java"));
	}
}
