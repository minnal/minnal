/**
 * 
 */
package org.minnal.generator.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Repository;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.minnal.core.MinnalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ganeshs
 *
 */
public class PomGenerator extends AbstractGenerator {
	
	private String projectName;
	
	private Model model;
	
	private Set<Dependency> dependenciesToAdd = new HashSet<Dependency>();
	
	private Set<Dependency> dependenciesToRemove = new HashSet<Dependency>();
	
	private static final String MODEL_VERSION = "4.0.0";
	
	private static final String DEFAULT_VERSION = "0.0.1-SNAPSHOT";
	
	private static final String POM_FILE = "pom.xml";
	
	private static final String ACTIVE_JPA_RELEASES_REPO = "https://raw.github.com/ActiveJpa/activejpa/mvn-repo/releases";
	
	private static final String AUTOPOJO_RELEASES_REPO = "https://raw.github.com/minnal/autopojo/mvn-repo/releases";
	
	private static final String MINNAL_RELEASES_REPO = "https://raw.github.com/minnal/minnal/mvn-repo/releases";
	
	private static final Logger logger = LoggerFactory.getLogger(PomGenerator.class);
	
	public PomGenerator(File baseDir, String projectName) {
		super(baseDir);
		this.projectName = projectName;
	}
	
	@Override
	public void init() {
		super.init();
		File pom = new File(baseDir, POM_FILE);
		if (pom.exists()) {
			logger.trace("pom already exist. Reading it");
			
			MavenXpp3Reader reader = new MavenXpp3Reader();
			try {
				model = reader.read(new FileInputStream(pom));
			} catch (Exception e) {
				throw new MinnalException("Failed while reading the pom - " + pom.getName(), e);
			}
		} else {
			model = createMavenModel();
		}
		for (Dependency dependency : dependenciesToAdd) {
			if (! model.getDependencies().contains(dependency)) {
				model.addDependency(dependency);
			}
		}
		for (Dependency dependency : dependenciesToRemove) {
			model.removeDependency(dependency);
		}
	}
	
	private Model createMavenModel() {
		model = new Model();
		model.setArtifactId(projectName);
		model.setName(projectName);
		model.setModelVersion(MODEL_VERSION);
		model.setVersion(DEFAULT_VERSION);
		model.setGroupId("com." + projectName.toLowerCase());
		
		Repository repository = new Repository();
		repository.setId("activejpa-repo");
		repository.setUrl(ACTIVE_JPA_RELEASES_REPO);
		model.addRepository(repository);
		
		repository = new Repository();
		repository.setId("autopojo-repo");
		repository.setUrl(AUTOPOJO_RELEASES_REPO);
		model.addRepository(repository);
		
		repository = new Repository();
		repository.setId("minnal-repo");
		repository.setUrl(MINNAL_RELEASES_REPO);
		model.addRepository(repository);
		
		Build build = new Build();
		Plugin plugin = new Plugin();
		plugin.setGroupId("org.codehaus.mojo");
		plugin.setArtifactId("exec-maven-plugin");
		plugin.setVersion("1.2");
		
		Xpp3Dom configuration = new Xpp3Dom("configuration");
		Xpp3Dom executable = new Xpp3Dom("executable");
		executable.setValue("java");
		configuration.addChild(executable);
		Xpp3Dom arguments = new Xpp3Dom("arguments");
		Xpp3Dom classpath = new Xpp3Dom("argument");
		classpath.setValue("-classpath");
		arguments.addChild(classpath);
		arguments.addChild(new Xpp3Dom("classpath"));
		Xpp3Dom mainClass = new Xpp3Dom("argument");
		mainClass.setValue("${mainClass}");
		arguments.addChild(mainClass);
		configuration.addChild(arguments);
		plugin.setConfiguration(configuration);
		PluginExecution execution = new PluginExecution();
		execution.addGoal("java");
		plugin.addExecution(execution);
		
		build.addPlugin(plugin);
		model.setBuild(build);
		
		return model;
	}
	
	public void addDependency(String groupId, String artifactId, String version) {
		addDependency(groupId, artifactId, version, "compile");	
	}
	
	public void addDependency(String groupId, String artifactId, String version, String scope) {
		Dependency dependency = new Dependency();
		dependency.setArtifactId(artifactId);
		dependency.setGroupId(groupId);
		dependency.setVersion(version);
		dependency.setScope(scope);
		dependenciesToAdd.add(dependency);
	}

	@Override
	public void generate() {
		logger.info("Creating the pom file {}", POM_FILE);
		File pom = new File(baseDir, POM_FILE);
		try {
			logger.debug("Writing the generated pom file {}", POM_FILE);
			MavenXpp3Writer writer = new MavenXpp3Writer();
			writer.write(new FileWriter(pom), model);
		} catch (Exception e) {
			throw new MinnalException("Failed while writing the pom - " + pom.getName(), e);
		}
	}

}
