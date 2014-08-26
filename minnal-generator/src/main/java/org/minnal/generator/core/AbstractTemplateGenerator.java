/**
 *
 */
package org.minnal.generator.core;

import com.google.common.base.Charsets;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.minnal.generator.exception.MinnalGeneratorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;


/**
 * @author ganeshs
 */
public abstract class AbstractTemplateGenerator extends AbstractGenerator {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateGenerator.class);

    protected final static VelocityEngine engine;

    static {
        logger.debug("Loading the velocity templates");
        Properties properties = new Properties();
        properties.put("runtime.log.logsystem.class", "org.minnal.utils.Slf4jLogChute");
        engine = new VelocityEngine(properties);
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
    }

    public AbstractTemplateGenerator(File baseDir) {
        super(baseDir);
    }

    protected void writeFile(String content, File file) {
        logger.info("Creating the file {}", file.getAbsolutePath());
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
            writer.write(content);
        } catch (IOException e) {
            throw new MinnalGeneratorException(e);
        } finally {
            closeStream(writer);
        }
    }

    protected void writeFile(Template template, VelocityContext context, File file) {
        logger.info("Creating the file {}", file.getAbsolutePath());
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
            template.merge(context, writer);
        } catch (IOException e) {
            throw new MinnalGeneratorException(e);
        } finally {
            closeStream(writer);
        }
    }
}
